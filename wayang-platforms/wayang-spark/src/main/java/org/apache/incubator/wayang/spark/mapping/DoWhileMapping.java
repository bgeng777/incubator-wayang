package org.apache.incubator.wayang.spark.mapping;

import org.apache.incubator.wayang.basic.operators.DoWhileOperator;
import org.apache.incubator.wayang.basic.operators.LoopOperator;
import org.apache.incubator.wayang.core.function.PredicateDescriptor;
import org.apache.incubator.wayang.core.mapping.Mapping;
import org.apache.incubator.wayang.core.mapping.OperatorPattern;
import org.apache.incubator.wayang.core.mapping.PlanTransformation;
import org.apache.incubator.wayang.core.mapping.ReplacementSubplanFactory;
import org.apache.incubator.wayang.core.mapping.SubplanPattern;
import org.apache.incubator.wayang.core.types.DataSetType;
import org.apache.incubator.wayang.spark.operators.SparkDoWhileOperator;
import org.apache.incubator.wayang.spark.operators.SparkLoopOperator;
import org.apache.incubator.wayang.spark.platform.SparkPlatform;

import java.util.Collection;
import java.util.Collections;

/**
 * Mapping from {@link LoopOperator} to {@link SparkLoopOperator}.
 */
@SuppressWarnings("unchecked")
public class DoWhileMapping implements Mapping {

    @Override
    public Collection<PlanTransformation> getTransformations() {
        return Collections.singleton(new PlanTransformation(
                this.createSubplanPattern(),
                this.createReplacementSubplanFactory(),
                SparkPlatform.getInstance()
        ));
    }

    private SubplanPattern createSubplanPattern() {
        final OperatorPattern operatorPattern = new OperatorPattern(
                "loop", new DoWhileOperator<>(DataSetType.none(), DataSetType.none(), (PredicateDescriptor) null, 1), false
        );
        return SubplanPattern.createSingleton(operatorPattern);
    }

    private ReplacementSubplanFactory createReplacementSubplanFactory() {
        return new ReplacementSubplanFactory.OfSingleOperators<DoWhileOperator>(
                (matchedOperator, epoch) -> new SparkDoWhileOperator<>(matchedOperator).at(epoch)
        );
    }
}