package com.shemshur.querytracker.controller;

import com.shemshur.querytracker.query_processing.QueryExecutor;
import com.shemshur.querytracker.validation.ValidateValues;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.shemshur.querytracker.validation.ValidateValues.checkAllParams;


@RestController
@RequestMapping("")
public class MessageController {

    @GetMapping
    public Map<String, String> sendGetResponse(){
        HashMap result = new HashMap<String,String>();
        result.put("info", "send get/options request to see allowed parameters url+'/options'");
        return result;
    }

    @RequestMapping(value="/options", method={RequestMethod.OPTIONS,RequestMethod.GET})
    public Map<String, String> getOpptions() {
        HashMap result = new HashMap<String,String>();
        result.put("query", "required|Parameter that contains your db query");
        result.put("db_name", "required|Parameter of database name");
        result.put("execution_times", "default=1|Quantity of query check iterations");
        result.put("timeout", "default=100sec|Max value of query execution");
        return result;
    }

    @PostMapping("check")
    public Map<String, String> checkQuery(@RequestBody Map<String,String> message){
        HashMap result = new HashMap<String,String>();

        String paramQuery = message.get("query");
        String paramDbName = message.get("db_name");
        String paramExecutionTimes = message.get("execution_times");
        String paramTimeout = message.get("timeout");

        HashMap<String, Object> resultOfValidation = checkAllParams(paramQuery, paramDbName, paramExecutionTimes, paramTimeout);
        if(resultOfValidation.get("error")==null) {

            String query = (String) resultOfValidation.get("paramQuery");
            String dbName = (String) resultOfValidation.get("paramDbName");
            Integer executionTimes = (Integer) resultOfValidation.get("paramExecutionTimes");
            Integer timeout = (Integer) resultOfValidation.get("paramTimeout");

            QueryExecutor executor = new QueryExecutor(query, dbName, executionTimes, timeout);

            ArrayList<Long> results = null;
            try {
                results = executor.check();
                double avgExecTime = 0;
                if (results!=null) avgExecTime = ValidateValues.getAverageValue(results);

                result.put("avg_time",Double.toString(avgExecTime));
                result.put("paramExecutionTimes",Integer.toString(executionTimes));
            } catch (Exception e) {
                result.put("error",e.toString());
            }
        } else result.putAll(resultOfValidation);
        return result;
    }
}
