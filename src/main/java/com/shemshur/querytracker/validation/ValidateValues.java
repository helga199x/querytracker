package  com.shemshur.querytracker.validation;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ValidateValues {

    static private String paramDbNameErrorMessage = "input value of db was illegal";
    static private String paramQueryErrorMessage = "input value of query was illegal";
    static private String paramTimeoutErrorMessage = "input value of timeout was illegal";
    static private String paramExecutionTimesErrorMessage = "input value of execution times was illegal";
    static private String serverErrorMessage = "sorry, problems with server";
    static private String paramTimeoutInfoMessage = "input value of timeout was set by default = 1";
    static private String paramExecutionTimesInfoMessage = "input value of execution times was set by default = 1";
    private static final Logger logger = Logger.getLogger(ValidateValues.class);
    static private Map<String,Object> addToResponse(String key, Map<String,Object> response, String newErrorMessage) throws ClassCastException {
        String errorValues = (String)response.get(key);
        if(errorValues==null) errorValues = newErrorMessage;
        else errorValues += "\n" + newErrorMessage;
        response.put(key, errorValues);
        return response;
    }

    static private Map<String,Object> addErrorToResponse(Map<String,Object> response, String newErrorMessage) throws ClassCastException {
        addToResponse("error", response, newErrorMessage);
        return response;
    }

    static private Map<String,Object> addInfoToResponse(Map<String,Object> response, String newErrorMessage) throws ClassCastException {
        addToResponse("info", response, newErrorMessage);
        return response;
    }


    static public boolean checkDbName(String db_name){
        return (db_name!=null)&&(!db_name.isEmpty());
    }

    static public boolean checkQuery(String query){
        return (query!=null)&&(!query.isEmpty());
    }

    static public boolean checkTimeout(String timeout){
        try{
            Integer result = Integer.parseInt(timeout);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    static public boolean checkExecutionTimes(String executionTimes){
        try{
            Integer result = Integer.parseInt(executionTimes);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    static public double getAverageValue(ArrayList<Long> arrayList){
        int sum = 0;
        for (Long d : arrayList) sum += d;
        double average = sum / arrayList.size();
        return average;
    }

    static public HashMap<String, Object> checkAllParams(String paramQuery, String paramDbName, String paramExecutionTimes, String paramTimeout) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        int timeout = 0;
        int executionTimes = 1;
        try {
            if (!ValidateValues.checkDbName(paramDbName)) addErrorToResponse(result, paramDbNameErrorMessage);
            if (!ValidateValues.checkQuery(paramQuery)) addErrorToResponse(result, paramQueryErrorMessage);
            if (paramTimeout!=null) {
                if (!ValidateValues.checkTimeout(paramTimeout))  addErrorToResponse(result, paramTimeoutErrorMessage);
                else  timeout = Integer.parseInt(paramTimeout);
            } else addInfoToResponse(result, paramTimeoutInfoMessage);
            if (paramExecutionTimes!=null) {
                if(!ValidateValues.checkExecutionTimes(paramExecutionTimes)) addErrorToResponse(result, paramExecutionTimesErrorMessage);
                else executionTimes = Integer.parseInt(paramExecutionTimes);
            } else addInfoToResponse(result, paramExecutionTimesInfoMessage);
            if (result.get("error")==null) {
                result.put("paramQuery", paramQuery);
                result.put("paramDbName", paramDbName);
                result.put("paramExecutionTimes", executionTimes);
                result.put("paramTimeout", timeout);
            }
        } catch (ClassCastException e){
            logger.error("Problems with props preprocessing", e);
            result.put("error", serverErrorMessage);
        }
        return result;
    }
}
