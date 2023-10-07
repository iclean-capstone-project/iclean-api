package iclean.code.function.job.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.job.CreateJobRequest;
import iclean.code.data.dto.request.job.UpdateJobRequest;
import iclean.code.function.job.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("api/v1/job")
public class JobController {
    @Autowired
    private JobService jobService;

    @GetMapping
    public ResponseEntity<ResponseObject> getJobs() {
        return jobService.getJobs();
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ResponseObject> createJob(@RequestBody @Valid CreateJobRequest request,
                                                    @RequestPart MultipartFile imgJob) {
        return jobService.createJob(request, imgJob);
    }

    @PutMapping(value = "{jobId}")
    public ResponseEntity<ResponseObject> updateJob(@PathVariable("jobId") int jobId,
                                                    @RequestBody @Valid UpdateJobRequest request) {
        return jobService.updateJob(jobId, request);
    }

    @DeleteMapping(value = "{jobId}")
    public ResponseEntity<ResponseObject> deleteJob(@PathVariable("jobId") int jobId) {
        return jobService.deleteJob(jobId);
    }
}
