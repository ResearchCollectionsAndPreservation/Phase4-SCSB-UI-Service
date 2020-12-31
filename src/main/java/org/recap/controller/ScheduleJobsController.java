package org.recap.controller;

import org.apache.commons.lang3.StringUtils;
import org.recap.RecapCommonConstants;
import org.recap.RecapConstants;
import org.recap.model.jpa.JobEntity;
import org.recap.model.schedule.ScheduleJobRequest;
import org.recap.model.schedule.ScheduleJobResponse;
import org.recap.model.search.ScheduleJobsForm;
import org.recap.model.usermanagement.UserDetailsForm;
import org.recap.repository.jpa.JobDetailsRepository;
import org.recap.security.UserManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by rajeshbabuk on 4/4/17.
 */

@RestController
@RequestMapping("/jobs")
@CrossOrigin(allowedHeaders = "*", allowCredentials = "true")
public class ScheduleJobsController extends AbstractController {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleJobsController.class);

    @Autowired
    private JobDetailsRepository jobDetailsRepository;

    /**
     * Gets rest template.
     *
     * @return the rest template
     */
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    public JobDetailsRepository getJobDetailsRepository() {
        return jobDetailsRepository;
    }


    /**
     * Gets all the jobs information from scsb database and display them as rows in the jobs UI page.
     *
     * @return the string
     */
    @GetMapping("/jobs")
    public ScheduleJobsForm displayJobs(HttpServletRequest request) {
        String errorMessage = null;
        HttpSession session = request.getSession(false);
        ScheduleJobsForm scheduleJobsForm = new ScheduleJobsForm();
        UserDetailsForm userDetailsForm = getUserAuthUtil().getUserDetails(session, RecapConstants.BARCODE_RESTRICTED_PRIVILEGE);
        if (userDetailsForm.isSuperAdmin()) {
            List<JobEntity> jobEntities = getJobDetailsRepository().findAll();
            scheduleJobsForm.setJobEntities(jobEntities);
        } else {
            errorMessage = UserManagementService.unAuthorizedUser(session, RecapCommonConstants.SEARCH, logger);
            scheduleJobsForm.setErrorMessage(errorMessage);
        }
        return scheduleJobsForm;
    }

    /**
     * Passes information to the scsb-batch-scheduler microservice about the job whether to be schedule or unschedule.
     *
     * @param scheduleJobsForm the schedule jobs form
     * @return the model and view
     */
    @PostMapping("/jobs")
    public ScheduleJobsForm scheduleJob(@RequestBody ScheduleJobsForm scheduleJobsForm) {

        ScheduleJobResponse scheduleJobResponse = null;
        try {
            ScheduleJobRequest scheduleJobRequest = new ScheduleJobRequest();
            scheduleJobRequest.setJobId(scheduleJobsForm.getJobId());
            scheduleJobRequest.setJobName(scheduleJobsForm.getJobName());
            scheduleJobRequest.setCronExpression(scheduleJobsForm.getCronExpression());
            scheduleJobRequest.setScheduleType(scheduleJobsForm.getScheduleType());
            HttpEntity<ScheduleJobRequest> httpEntity = new HttpEntity<>(scheduleJobRequest, getRestHeaderService().getHttpHeaders());

            ResponseEntity<ScheduleJobResponse> responseEntity = getRestTemplate().exchange(getScsbUrl() + RecapCommonConstants.URL_SCHEDULE_JOBS, HttpMethod.POST, httpEntity, ScheduleJobResponse.class);
            scheduleJobResponse = responseEntity.getBody();
            String message = scheduleJobResponse.getMessage();
            if (StringUtils.containsIgnoreCase(message, RecapCommonConstants.SUCCESS)) {
                JobEntity jobEntity = jobDetailsRepository.findByJobName(scheduleJobsForm.getJobName());
                if (null != jobEntity) {
                    if (RecapConstants.UNSCHEDULE.equals(scheduleJobsForm.getScheduleType())) {
                        jobEntity.setStatus(RecapConstants.UNSCHEDULED);
                        jobEntity.setNextRunTime(null);
                    } else {
                        jobEntity.setStatus(RecapConstants.SCHEDULED);
                        jobEntity.setCronExpression(scheduleJobsForm.getCronExpression());
                        jobEntity.setNextRunTime(scheduleJobResponse.getNextRunTime());
                    }
                    jobDetailsRepository.save(jobEntity);
                }
                scheduleJobsForm.setMessage(scheduleJobResponse.getMessage());
            } else {
                scheduleJobsForm.setErrorMessage(scheduleJobResponse.getMessage());
            }
        } catch (Exception e) {
            logger.error(RecapCommonConstants.LOG_ERROR, e);
            scheduleJobsForm.setErrorMessage(e.getMessage());
        }
        return scheduleJobsForm;
    }
}
