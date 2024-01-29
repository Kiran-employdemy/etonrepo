package com.eaton.platform.core.workflows;

import com.adobe.granite.workflow.WorkflowException;
import com.adobe.granite.workflow.WorkflowSession;
import com.adobe.granite.workflow.exec.WorkItem;
import com.adobe.granite.workflow.exec.WorkflowProcess;
import com.adobe.granite.workflow.metadata.MetaDataMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.jcr.Binary;
import javax.jcr.Node;
import javax.jcr.Session;

import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(property = {"service.description=Adaptive Form - Convert JSON Comma String to JSON String Array", "service.vendor=Eaton", "process.label=Adaptive Form - Convert Comma String JSON to Array String JSON"})

public class ConvertCommaStringToArrayAdaptiveFormProcess implements WorkflowProcess {

    private static final Logger logger = LoggerFactory.getLogger(ConvertCommaStringToArrayAdaptiveFormProcess.class);

    public void execute(WorkItem workItem, WorkflowSession workflowSession, MetaDataMap args) throws WorkflowException {
        try {
            logger.debug("[Custom Workflow] Entered into ConvertCommaStringToArrayAdaptiveFormProcess.execute()");

            String constantProcessArgs = "PROCESS_ARGS";
            String constantJsonFileContent = "/Data.json/jcr:content";

            String payloadPath = null;
            String dataFilePath = null;

            String[] inputArguments = new String[args.size()];

            Session session = null;
            Node submittedDataNode = null;

            String jsonPropertyAttribute = "jcr:data";
            String jsonLevel1 = "afData";
            String jsonLevel2 = "afBoundData";
            String jsonLevel3 = "data";
            JsonParser jsonParser = new JsonParser();

            JsonObject fullFormJson = new JsonObject();

            String orderCenterPropertyStartString = "oc";
            String orderCenterObjectName = "ocAccounts";

            if (args != null && args.get(constantProcessArgs, "STRING").contains(",")) {
                logger.debug("[Custom Workflow] Splitting out the multiple input arguments");
                inputArguments = args.get(constantProcessArgs, "STRING").split(",");
            }
            else {
                if (args.get(constantProcessArgs, "STRING") != null) {
                    logger.debug("[Custom Workflow] Received a single input argument");
                    inputArguments[0] = args.get(constantProcessArgs, "STRING");
                }
                else {
                    logger.error("[Custom Workflow Error] Received no input arguments from workflow configuration");
                }
            }

            logger.debug("[Custom Workflow] Received the following input arguments for the fields: " + Arrays.toString(inputArguments));

            if (inputArguments != null && !inputArguments.toString().isEmpty()) {
                if (workItem.getWorkflowData() != null && workItem.getWorkflowData().getPayload() != null) {
                    payloadPath = workItem.getWorkflowData().getPayload().toString();
                    logger.debug("[Custom Workflow] Retrieved the payloadPath: " + payloadPath);

                    dataFilePath = payloadPath + constantJsonFileContent;
                    logger.debug("[Custom Workflow] Calculated the dataFilePath: " + dataFilePath);
                }
                else {
                    logger.error("[Custom Workflow Error] Unable to retrieve payload path from workflow data");
                }

                if (workflowSession.adaptTo(Session.class) != null) {
                    session = (Session) workflowSession.adaptTo(Session.class);
                }
                else {
                    logger.error("[Custom Workflow Error] Unable to retrieve workflow session");
                }

                if (session.getNode(dataFilePath) != null) {
                    submittedDataNode = session.getNode(dataFilePath);
                }
                else {
                    logger.error("[Custom Workflow Error] Unable to retrieve node at dataFilePath ("+dataFilePath+")");
                }

                if (submittedDataNode.getProperty(jsonPropertyAttribute) != null) {
                    InputStream submittedDataStream = submittedDataNode.getProperty(jsonPropertyAttribute).getBinary().getStream();
                    BufferedReader streamReader = new BufferedReader(new InputStreamReader(submittedDataStream, "UTF-8"));
                    StringBuilder stringBuilder = new StringBuilder();
                    String inputStr;
                    while ((inputStr = streamReader.readLine()) != null) {
                        stringBuilder.append(inputStr);
                    }
                    fullFormJson = jsonParser.parse(stringBuilder.toString()).getAsJsonObject();
                    logger.debug("[Custom Workflow] Parsed out the following fullFormJson: " + fullFormJson.toString());
                }
                else {
                    logger.error("[Custom Workflow Error] Unable to find form JSON data at jsonPropertyAttribute ("+jsonPropertyAttribute+")");
                }

                if (fullFormJson.getAsJsonObject(jsonLevel1) != null) {
                    JsonObject level1Json = fullFormJson.getAsJsonObject(jsonLevel1);
                    if (level1Json.getAsJsonObject(jsonLevel2) != null) {
                        JsonObject level2Json = level1Json.getAsJsonObject(jsonLevel2);
                        if (level2Json.getAsJsonObject(jsonLevel3) != null) {
                            JsonObject level3Json = level2Json.getAsJsonObject(jsonLevel3);
                            if (!level3Json.keySet().isEmpty()) {
                                int currentIndex = 1;
                                for (String currentKey : level3Json.keySet()) {
                                    JsonObject currentJsonObject = level3Json.getAsJsonObject(currentKey);
                                    JsonArray orderCenterJsonArray = new JsonArray();
                                    for (String currentAttributeToUpdate : inputArguments) {
                                        boolean processingOrderCenterAttribute = false;
                                        if (currentAttributeToUpdate.startsWith(orderCenterPropertyStartString) && currentJsonObject.getAsJsonArray(orderCenterObjectName) != null) {
                                            orderCenterJsonArray = currentJsonObject.getAsJsonArray(orderCenterObjectName);
                                            processingOrderCenterAttribute = true;
                                            logger.debug("[Custom Workflow] Order Center Attribute - captured the JSON array ("+orderCenterObjectName+") inside of ("+currentKey+")");
                                        }
                                        if (currentJsonObject.get(currentAttributeToUpdate) != null) {
                                            logger.debug("[Custom Workflow] Found the attribute ("+currentAttributeToUpdate+") inside the object ("+currentKey+")");
                                            String currentAttributeValue = currentJsonObject.get(currentAttributeToUpdate).toString();
                                            logger.debug("[Custom Workflow] Current value (STRING) of this attribute is: " + currentAttributeValue);

                                            currentJsonObject.add(currentAttributeToUpdate,(JsonElement) convertValueToJsonArray(currentAttributeValue));
                                        }
                                        else if (processingOrderCenterAttribute) {
                                            Iterator<JsonElement> orderCenterIterator = orderCenterJsonArray.iterator();
                                            while (orderCenterIterator.hasNext()) {
                                                JsonElement currentOrderCenterObject = orderCenterIterator.next();

                                                if (currentOrderCenterObject.getAsJsonObject() != null && currentOrderCenterObject.getAsJsonObject().get(currentAttributeToUpdate) != null) {
                                                    logger.debug("[Custom Workflow] Found the attribute ("+currentAttributeToUpdate+") inside the object ("+orderCenterObjectName+")");
                                                    String currentAttributeValue = currentOrderCenterObject.getAsJsonObject().get(currentAttributeToUpdate).toString();
                                                    logger.debug("[Custom Workflow] Current value (STRING) of this attribute is: " + currentAttributeValue);

                                                    currentOrderCenterObject.getAsJsonObject().add(currentAttributeToUpdate, (JsonElement) convertValueToJsonArray(currentAttributeValue));
                                                }
                                            }
                                        }
                                    }

                                    if (currentIndex == level3Json.keySet().size()) {
                                        logger.debug("[Custom Workflow] Updated fullFormJson: " + fullFormJson);
                                        InputStream inputStream = new ByteArrayInputStream(fullFormJson.toString().getBytes(StandardCharsets.UTF_8));
                                        Binary binary = session.getValueFactory().createBinary(inputStream);
                                        submittedDataNode.setProperty(jsonPropertyAttribute, binary);
                                        session.save();
                                        logger.debug("[Custom Workflow Complete] FINISHED!");
                                    }

                                    currentIndex++;
                                }
                            }
                            else {
                                logger.error("[Custom Workflow Error] No entries found within ("+jsonLevel1+"."+jsonLevel2+"."+jsonLevel3+")");
                            }
                        }
                        else {
                            logger.error("[Custom Workflow Error] Unable to find data in fullFormJson at ("+jsonLevel1+"."+jsonLevel2+"."+jsonLevel3+")");
                        }
                    }
                    else {
                        logger.error("[Custom Workflow Error] Unable to find data in fullFormJson at ("+jsonLevel1+"."+jsonLevel2+")");
                    }
                }
                else {
                    logger.error("[Custom Workflow Error] Unable to find data in fullFormJson at ("+jsonLevel1+")");
                }
            }
            else {
                logger.error("[Custom Workflow Error] Skipped processing due to missing input arguments from workflow configuration");
            }

        }
        catch (Exception exception) {
            logger.error("[Custom Workflow Error] Exception in ConvertCommaStringToArrayAdaptiveFormProcess.execute()",exception);
        }
    }

    private JsonArray convertValueToJsonArray(String inputValue) {
        try {
            if (inputValue.contains("\"")) {
                inputValue = inputValue.replaceAll("\"","").trim();
                logger.debug("[Custom Workflow] Removed those pesky quotation marks from this value: " + inputValue);
            }
            if (inputValue.contains("[") || inputValue.contains("]")) {
                inputValue = inputValue.replaceAll("\\[","").replaceAll("\\]","").trim();
                logger.debug("[Custom Workflow] Removed those pesky square brackets from this value: " + inputValue);
            }
            if (inputValue.contains("\\n")) {
                inputValue = inputValue.replaceAll("\\\\n",",");
                logger.debug("[Custom Workflow] Replaced improper seperator for this value: " + inputValue);
            }
            JsonArray newJsonArray = new JsonArray();
            if (inputValue.contains(",")) {
                String[] splitValues = inputValue.split(",");
                for (String currentValue : splitValues) {
                    newJsonArray.add(currentValue);
                }
                logger.debug("[Custom Workflow] New value (ARRAY) for this attribute is: " + newJsonArray);
            }
            else {
                newJsonArray.add(inputValue);
                logger.debug("[Custom Workflow] New value (ARRAY) for this attribute is: " + newJsonArray);
            }
            return newJsonArray;
        }
        catch (Exception exception) {
            logger.error("[Custom Workflow Error] Exception in ConvertCommaStringToArrayAdaptiveFormProcess.convertValueToJsonArray()",exception);
            return null;
        }
    }
}

