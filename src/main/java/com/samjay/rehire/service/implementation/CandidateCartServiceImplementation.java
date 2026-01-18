package com.samjay.rehire.service.implementation;

import com.samjay.rehire.constants.CartType;
import com.samjay.rehire.dto.cart.CartRequest;
import com.samjay.rehire.dto.email.GeneratedEmailDto;
import com.samjay.rehire.dto.email.SendBulkEmailRequest;
import com.samjay.rehire.dto.job.JobApplicationDto;
import com.samjay.rehire.exception.ApplicationException;
import com.samjay.rehire.langchain4j.LangChain4jAssistant;
import com.samjay.rehire.model.CandidateCart;
import com.samjay.rehire.model.Job;
import com.samjay.rehire.model.JobApplication;
import com.samjay.rehire.model.Organization;
import com.samjay.rehire.repository.CandidateCartRepository;
import com.samjay.rehire.repository.JobApplicationRepository;
import com.samjay.rehire.repository.JobRepository;
import com.samjay.rehire.repository.OrganizationRepository;
import com.samjay.rehire.service.CandidateCartService;
import com.samjay.rehire.service.EmailService;
import com.samjay.rehire.util.ModelMapper;
import dev.langchain4j.model.input.PromptTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.framework.AopContext;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CandidateCartServiceImplementation implements CandidateCartService {

    private final CandidateCartRepository candidateCartRepository;

    private final JobRepository jobRepository;

    private final JobApplicationRepository jobApplicationRepository;

    private final OrganizationRepository organizationRepository;

    private final ModelMapper modelMapper;

    private final LangChain4jAssistant langChain4jAssistant;

    private final EmailService emailService;

    private static final String EMAIL_CONTENT_PROMPT = """
            Generate a professional, generalized and empathetic email content for a candidate who has been {status} for a job.
            IMPORTANT: Use {name} literally as a placeholder for the candidate's name (do not replace it).
            
            Job Details:
            Title: {jobTitle}
            Organization: {organizationName}
            
            Requirements:
            1. Be professional and courteous.
            2. For accepted candidates, express enthusiasm.
            3. For rejected candidates, be encouraging and constructive.
            4. Keep the tone positive and supportive.
            5. Do not add any explanations after the email body.
            6. Return ONLY the email body text in JSON as { "body": "..." }.
            """;


    @Transactional
    @Override
    public void addCandidates(CartRequest addToCartRequest) {

        Organization organization = getAuthenticatedOrganization();

        Job job = getJobById(addToCartRequest.getJobId());

        List<JobApplication> candidates = jobApplicationRepository.findAllById(addToCartRequest.getCandidateIds());

        CandidateCart cart = candidateCartRepository.findByOrganizationAndJobAndCartType(organization, job, addToCartRequest.getCartType())
                .orElseGet(() -> {

                    CandidateCart newCart = new CandidateCart();

                    newCart.setOrganization(organization);

                    newCart.setJob(job);

                    newCart.setCartType(addToCartRequest.getCartType());

                    newCart.setCandidates(new ArrayList<>());

                    return newCart;

                });

        CartType oppositeType = (addToCartRequest.getCartType() == CartType.ACCEPTANCE) ? CartType.REJECTION : CartType.ACCEPTANCE;

        CandidateCart oppositeCart = candidateCartRepository.findByOrganizationAndJobAndCartType(organization, job, oppositeType)
                .orElse(null);

        for (JobApplication candidate : candidates) {

            boolean inOppositeCart = oppositeCart != null && oppositeCart.getCandidates().contains(candidate);

            if (!cart.getCandidates().contains(candidate) && !inOppositeCart) {

                cart.addCandidate(candidate);

            }
        }

        organization.addCandidateCart(cart);

        organizationRepository.save(organization);
    }

    @Override
    public List<JobApplicationDto> getCartCandidates(UUID jobId, CartType cartType) {

        Organization organization = getAuthenticatedOrganization();

        Job job = getJobById(jobId);

        CandidateCart cart = candidateCartRepository.findByOrganizationAndJobAndCartType(organization, job, cartType)
                .orElse(null);

        if (cart == null) return List.of();

        return cart.getCandidates()
                .stream()
                .map(modelMapper::toJobApplicationDto)
                .toList();
    }

    @Override
    public void removeCandidates(CartRequest removeFromCartRequest) {

        Organization organization = getAuthenticatedOrganization();

        Job job = getJobById(removeFromCartRequest.getJobId());

        List<JobApplication> candidates = jobApplicationRepository.findAllById(removeFromCartRequest.getCandidateIds());

        CandidateCart cart = candidateCartRepository
                .findByOrganizationAndJobAndCartType(organization, job, removeFromCartRequest.getCartType())
                .orElseThrow();

        for (JobApplication candidate : candidates) {

            cart.removeJobApplication(candidate);

        }

        organizationRepository.save(organization);
    }

    @Override
    public GeneratedEmailDto generateEmail(UUID jobId, CartType cartType) {

        Organization organization = getAuthenticatedOrganization();

        Job job = getJobById(jobId);

        CandidateCart cart = candidateCartRepository.findByOrganizationAndJobAndCartType(organization, job, cartType)
                .orElseThrow(() -> new ApplicationException("Could not find cart", HttpStatus.BAD_REQUEST));

        String status = cart.getCartType() == CartType.ACCEPTANCE ? "Accepted" : "Rejected";

        PromptTemplate promptTemplate = PromptTemplate.from(EMAIL_CONTENT_PROMPT);

        String prompt = promptTemplate.apply(Map.of(
                "status", status,
                "jobTitle", job.getTitle(),
                "organizationName", organization.getName()
        )).text();

        String subject = organization.getName().toUpperCase() + " RECRUITMENT";

        String body = langChain4jAssistant.generateEmailBody(prompt).body();

        return new GeneratedEmailDto(subject, body);

    }

    @Override
    public String sendBulkEmails(SendBulkEmailRequest sendBulkEmailRequest) {

        Organization organization = getAuthenticatedOrganization();

        Job job = getJobById(sendBulkEmailRequest.getJobId());

        CandidateCart cart = candidateCartRepository.findByOrganizationAndJobAndCartType(organization, job, sendBulkEmailRequest.getCartType())
                .orElseThrow(() -> new ApplicationException("Could not find cart", HttpStatus.BAD_REQUEST));

        List<JobApplication> candidates = cart.getCandidates();

        int batchSize = 500;

        List<List<JobApplication>> batches = new ArrayList<>();

        for (int i = 0; i < candidates.size(); i += batchSize) {

            batches.add(candidates.subList(i, Math.min(i + batchSize, candidates.size())));

        }

        for (List<JobApplication> batch : batches) {

            batch.parallelStream().forEach(candidate -> {

                try {

                    String personalizedBody = sendBulkEmailRequest.getGeneratedEmailRequestDto()
                            .getBody().
                            replace("{name}", candidate.getFullName());

                    emailService.queueEmails(candidate.getEmail(), sendBulkEmailRequest.getGeneratedEmailRequestDto()
                            .getSubject(), personalizedBody);

                    candidate.markAsEmailFinalized();

                    jobApplicationRepository.save(candidate);


                } catch (Exception e) {

                    throw new ApplicationException("Error sending email", HttpStatus.BAD_REQUEST);

                }
            });

            try {

                Thread.sleep(200);

            } catch (InterruptedException e) {

                Thread.currentThread().interrupt();

            }
        }

        return "Successfully sent email to candidates";
    }

    @Override
    public void moveCandidatesBetweenCarts(CartRequest moveBetweenCartRequest) {

        List<JobApplication> candidates = jobApplicationRepository.findAllById(moveBetweenCartRequest.getCandidateIds());

        for (JobApplication candidate : candidates) {

            CandidateCart candidateCart = candidate.getCart();

            if (candidateCart != null) {

                candidateCart.removeJobApplication(candidate);

                candidateCartRepository.save(candidateCart);

            }
        }

        ((CandidateCartService) AopContext.currentProxy()).addCandidates(moveBetweenCartRequest);
    }

    private Organization getAuthenticatedOrganization() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        return organizationRepository.findByEmail(email)
                .orElseThrow(() -> new ApplicationException("Organization not found", HttpStatus.BAD_REQUEST));
    }

    private Job getJobById(UUID jobId) {

        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ApplicationException("Job not found", HttpStatus.BAD_REQUEST));
    }
}