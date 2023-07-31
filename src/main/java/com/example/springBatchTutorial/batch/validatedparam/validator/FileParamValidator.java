package com.example.springBatchTutorial.batch.validatedparam.validator;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;
import org.springframework.util.StringUtils;

public class FileParamValidator implements JobParametersValidator {

    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        String fileName = parameters.getString("fileName");  //파라미터 명에 해당하는 값을 받아오기
        /**
         * fileName이 csv파일이 맞는지 검증
         * 이때 StringUtils.endsWithIgnoreCase사용
         */
        if (!StringUtils.endsWithIgnoreCase(fileName, "csv")) {  //csv로 끝나는게 아니라면!!
            //csv로 끝나지 않으면 이 부분이 실행되어 예외를 던지면 돼
            throw new JobParametersInvalidException("this is not csv fille");
        }
    }
}
