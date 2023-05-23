package h01;

import org.sourcegrade.jagr.api.rubric.Criterion;
import org.sourcegrade.jagr.api.rubric.JUnitTestRef;
import org.sourcegrade.jagr.api.rubric.Rubric;
import org.sourcegrade.jagr.api.rubric.RubricProvider;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;

public class H01_RubricProvider implements RubricProvider {

    public static final Rubric RUBRIC = Rubric.builder()
        .title("H01 | Foreign Contaminants")
        .addChildCriteria(
            Criterion.builder()
                .shortDescription("H1 | Steuerung des \"CleaningRobot\"")
                .addChildCriteria(
                    RubricUtils.criterion(
                        "Wenn die übergebene Direction < 0 oder > 3 ist, so bleibt der Roboter stehen.",
                        JUnitTestRef.ofMethod(() -> CleaningRobotTest.class.getDeclaredMethod("testMovementInvalidDirection", JsonParameterSet.class))
                    ),
                    RubricUtils.criterion(
                        "Wenn die übergebene Direction >= 0 und < 4 ist, dann schaut der Roboter in die entsprechende Richtung.",
                        JUnitTestRef.ofMethod(() -> CleaningRobotTest.class.getDeclaredMethod("testMovementValidDirectionRotation", JsonParameterSet.class))
                    ),
                    RubricUtils.criterion(
                        "Wenn die übergebene Direction >= 0 und < 4 ist, so bewegt sich der Roboter in die entsprechende Richtung, falls der Weg frei ist.",
                        JUnitTestRef.ofMethod(() -> CleaningRobotTest.class.getDeclaredMethod("testMovementValidDirectionHasMoved", JsonParameterSet.class))
                    )
                )
                .build()
        )
        .build();

    @Override
    public Rubric getRubric() {
        return RUBRIC;
    }
}
