package com.athletiq.backend.objectiveevaluation.service;

import com.athletiq.backend.application.entity.Application;
import com.athletiq.backend.form.entity.FormField;
import com.athletiq.backend.form.repository.FormFieldRepository;
import com.athletiq.backend.objectiveevaluation.dto.NormalizedAnswer;
import com.athletiq.backend.objectiveevaluation.dto.NormalizedApplicationAnswers;
import org.springframework.stereotype.Service;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class ApplicationAnswerNormalizationService {

    private final JsonMapper jsonMapper;

    private final FormFieldRepository formFieldRepository;

    public ApplicationAnswerNormalizationService(
            JsonMapper jsonMapper,
            FormFieldRepository formFieldRepository
    ) {

        this.jsonMapper =
                jsonMapper;

        this.formFieldRepository =
                formFieldRepository;
    }

    public NormalizedApplicationAnswers normalize(
            Application application
    ) {

        if(application == null){

            throw new IllegalArgumentException(
                    "Application is required."
            );
        }

        if(application.getId() == null){

            throw new IllegalArgumentException(
                    "Application ID is required."
            );
        }

        if(
                application.getFormVersion() == null ||
                application.getFormVersion().getId() == null
        ){

            throw new IllegalArgumentException(
                    "Application FormVersion is required."
            );
        }

        Map<String,Object> submittedAnswers =
                parseSubmittedAnswers(
                        application.getSubmittedData()
                );

        List<FormField> fields =
                formFieldRepository
                        .findByFormVersionIdOrderByDisplayOrderAsc(
                                application.getFormVersion().getId()
                        );

        Map<String,NormalizedAnswer> normalized =
                new LinkedHashMap<>();

        List<String> missing =
                new ArrayList<>();

        for(FormField field : fields){

            String fieldKey =
                    normalizeFieldKey(
                            field.getFieldKey()
                    );

            if(fieldKey == null){
                continue;
            }

            Object rawValue =
                    submittedAnswers.get(
                            fieldKey
                    );

            boolean present =
                    rawValue != null;

            if(!present){

                missing.add(
                        fieldKey
                );

                normalized.put(
                        fieldKey,
                        new NormalizedAnswer(
                                fieldKey,
                                fieldType(field),
                                null,
                                null,
                                false
                        )
                );

                continue;
            }

            Object normalizedValue =
                    normalizeValue(
                            field,
                            rawValue
                    );

            String originalValue =
                    originalValueString(
                            rawValue
                    );

            normalized.put(
                    fieldKey,
                    new NormalizedAnswer(
                            fieldKey,
                            fieldType(field),
                            originalValue,
                            normalizedValue,
                            true
                    )
            );
        }

        return new NormalizedApplicationAnswers(
                application.getId(),
                application.getFormVersion().getId(),
                Collections.unmodifiableMap(
                        normalized
                ),
                List.copyOf(
                        missing
                )
        );
    }

    private Map<String,Object> parseSubmittedAnswers(
            String submittedData
    ){

        if(
                submittedData == null ||
                submittedData.isBlank()
        ){

            return Collections.emptyMap();
        }

        try{

            Map<String,Object> result =
                    jsonMapper.readValue(
                            submittedData,
                            new TypeReference<
                                    Map<String,Object>
                                    >(){}
                    );

            if(result == null){
                return Collections.emptyMap();
            }

            Map<String,Object> normalized =
                    new LinkedHashMap<>();

            result.forEach(
                    (key,value) -> {

                        String normalizedKey =
                                normalizeFieldKey(
                                        key
                                );

                        if(normalizedKey != null){

                            normalized.put(
                                    normalizedKey,
                                    value
                            );
                        }
                    }
            );

            return normalized;

        }catch(Exception exception){

            throw new IllegalArgumentException(
                    "Submitted application data contains invalid JSON.",
                    exception
            );
        }
    }

    private Object normalizeValue(
            FormField field,
            Object value
    ){

        String type =
                fieldType(field)
                        .toUpperCase(
                                Locale.ROOT
                        );

        return switch(type){

            case "NUMBER",
                 "NUMERIC",
                 "INTEGER",
                 "DECIMAL",
                 "RATING" ->
                    normalizeNumeric(
                            value
                    );

            case "BOOLEAN",
                 "CHECKBOX" ->
                    normalizeBoolean(
                            value
                    );

            case "MULTI_SELECT",
                 "MULTISELECT",
                 "CHECKBOX_GROUP" ->
                    normalizeList(
                            value
                    );

            case "DATE" ->
                    normalizeDate(
                            value
                    );

            case "TEXT",
                 "LONG_TEXT",
                 "EMAIL",
                 "PHONE",
                 "URL",
                 "SELECT",
                 "DROPDOWN",
                 "RADIO",
                 "TEXTAREA",
                 "FILE",
                 "IMAGE" ->
                    normalizeText(
                            value
                    );

            default ->
                    normalizeGeneric(
                            value
                    );
        };
    }

    private String normalizeText(
            Object value
    ){

        if(value == null){
            return null;
        }

        if(value instanceof String stringValue){

            return stringValue.trim();
        }

        return String.valueOf(
                value
        ).trim();
    }

    private BigDecimal normalizeNumeric(
            Object value
    ){

        if(value == null){
            return null;
        }

        if(value instanceof BigDecimal decimal){
            return decimal;
        }

        if(value instanceof Number number){

            return new BigDecimal(
                    number.toString()
            );
        }

        String text =
                String.valueOf(
                        value
                ).trim();

        if(text.isEmpty()){
            return null;
        }

        try{

            return new BigDecimal(
                    text
            );

        }catch(NumberFormatException exception){

            throw new IllegalArgumentException(
                    "Invalid numeric answer: " +
                            text
            );
        }
    }

    private Boolean normalizeBoolean(
            Object value
    ){

        if(value instanceof Boolean booleanValue){
            return booleanValue;
        }

        if(value instanceof Number number){

            if(number.intValue() == 1){
                return true;
            }

            if(number.intValue() == 0){
                return false;
            }
        }

        String text =
                String.valueOf(
                        value
                )
                .trim()
                .toLowerCase(
                        Locale.ROOT
                );

        if(
                "true".equals(text) ||
                "yes".equals(text) ||
                "1".equals(text)
        ){

            return true;
        }

        if(
                "false".equals(text) ||
                "no".equals(text) ||
                "0".equals(text)
        ){

            return false;
        }

        throw new IllegalArgumentException(
                "Invalid boolean answer: " +
                        value
        );
    }

    private List<Object> normalizeList(
            Object value
    ){

        if(value instanceof List<?> list){

            List<Object> normalized =
                    new ArrayList<>();

            for(Object item : list){

                Object itemValue =
                        normalizeGeneric(
                                item
                        );

                if(itemValue != null){
                    normalized.add(
                            itemValue
                    );
                }
            }

            return List.copyOf(
                    normalized
            );
        }

        String text =
                normalizeText(
                        value
                );

        if(text == null || text.isBlank()){
            return List.of();
        }

        String[] parts =
                text.split(",");

        List<Object> normalized =
                new ArrayList<>();

        for(String part : parts){

            String item =
                    part.trim();

            if(!item.isEmpty()){

                normalized.add(
                        item
                );
            }
        }

        return List.copyOf(
                normalized
        );
    }

    private String normalizeDate(
            Object value
    ){

        String text =
                normalizeText(
                        value
                );

        if(text == null || text.isBlank()){
            return null;
        }

        try{

            LocalDate.parse(
                    text
            );

            return text;

        }catch(Exception exception){

            throw new IllegalArgumentException(
                    "Invalid date answer: " +
                            text
            );
        }
    }

    private Object normalizeGeneric(
            Object value
    ){

        if(value == null){
            return null;
        }

        if(value instanceof String stringValue){
            return stringValue.trim();
        }

        if(value instanceof Number number){

            if(
                    value instanceof Float ||
                    value instanceof Double
            ){

                return new BigDecimal(
                        number.toString()
                );
            }

            return number;
        }

        if(value instanceof Boolean){
            return value;
        }

        if(value instanceof List<?> list){

            List<Object> normalized =
                    new ArrayList<>();

            for(Object item : list){

                normalized.add(
                        normalizeGeneric(
                                item
                        )
                );
            }

            return List.copyOf(
                    normalized
            );
        }

        if(value instanceof Map<?,?> map){

            Map<String,Object> normalized =
                    new LinkedHashMap<>();

            for(
                    Map.Entry<?,?> entry :
                    map.entrySet()
            ){

                if(entry.getKey() == null){
                    continue;
                }

                normalized.put(
                        String.valueOf(
                                entry.getKey()
                        ),
                        normalizeGeneric(
                                entry.getValue()
                        )
                );
            }

            return Map.copyOf(
                    normalized
            );
        }

        return value;
    }

    private String originalValueString(
            Object value
    ){

        if(value == null){
            return null;
        }

        if(value instanceof String stringValue){
            return stringValue;
        }

        try{

            return jsonMapper.writeValueAsString(
                    value
            );

        }catch(Exception exception){

            return String.valueOf(
                    value
            );
        }
    }

    private String normalizeFieldKey(
            String key
    ){

        if(key == null){
            return null;
        }

        String normalized =
                key.trim()
                        .toLowerCase(
                                Locale.ROOT
                        );

        return normalized.isEmpty()
                ? null
                : normalized;
    }

    private String fieldType(
            FormField field
    ){

        if(
                field == null ||
                field.getFieldType() == null
        ){

            return "";
        }

        return field.getFieldType()
                .name();
    }
}