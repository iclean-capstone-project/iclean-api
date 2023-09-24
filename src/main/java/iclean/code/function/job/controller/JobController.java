package iclean.code.function.job.controller;

import iclean.code.data.dto.common.ResponseObject;
import iclean.code.data.dto.request.job.AddJobRequest;
import iclean.code.data.dto.request.job.UpdateJobRequest;
import iclean.code.function.job.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/job")
public class JobController {
    @Autowired
    private JobService jobService;

    @GetMapping(value = "/get")
    public ResponseEntity<ResponseObject> getAllJob() {
        return jobService.getAllJob();
    }

    @PostMapping(value = "/post")
    public ResponseEntity<ResponseObject> addJob(@RequestBody @Valid AddJobRequest request) {
        return jobService.addJob(request);
    }

    @PutMapping(value = "/put")
    public ResponseEntity<ResponseObject> updateJob(@RequestBody @Valid UpdateJobRequest request) {
        return jobService.updateJob(request);
    }

    @DeleteMapping(value = "/delete")
    public ResponseEntity<ResponseObject> deleteJob(@RequestParam @Valid int jobId) {
        return jobService.deleteJob(jobId);
    }
}
