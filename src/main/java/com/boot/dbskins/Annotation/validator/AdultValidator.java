package com.boot.dbskins.Annotation.validator;

import com.boot.dbskins.Annotation.IsAdult;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
public class AdultValidator implements ConstraintValidator<IsAdult, Integer> {

    @Override
    public void initialize(IsAdult constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext constraintValidatorContext) {
        return value >= 18;
    }
}
