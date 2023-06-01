package h01;

import org.sourcegrade.jagr.api.rubric.Criterion;
import org.sourcegrade.jagr.api.rubric.JUnitTestRef;
import org.sourcegrade.jagr.api.rubric.Rubric;
import org.sourcegrade.jagr.api.rubric.RubricProvider;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;

import static org.tudalgo.algoutils.tutor.general.jagr.RubricUtils.criterion;


public class H01_RubricProvider implements RubricProvider {

    public static final Rubric RUBRIC = Rubric.builder()
        .title("H01 | Foreign Contaminants")
        .addChildCriteria(
            Criterion.builder()
                .shortDescription("H1 | Steuerung des \"CleaningRobot\"")
                .addChildCriteria(
                    criterion(
                        "Wenn die übergebene Direction < 0 oder > 3 ist, so bleibt der Roboter stehen.",
                        JUnitTestRef.ofMethod(() -> CleaningRobotTest.class.getDeclaredMethod("testMovementInvalidDirection", JsonParameterSet.class))
                    ),
                    criterion(
                        "Wenn die übergebene Direction >= 0 und < 4 ist, dann schaut der Roboter in die entsprechende Richtung.",
                        JUnitTestRef.ofMethod(() -> CleaningRobotTest.class.getDeclaredMethod("testMovementValidDirectionRotation", JsonParameterSet.class))
                    ),
                    criterion(
                        "Wenn die übergebene Direction >= 0 und < 4 ist, so bewegt sich der Roboter in die entsprechende Richtung, falls der Weg frei ist.",
                        JUnitTestRef.ofMethod(() -> CleaningRobotTest.class.getDeclaredMethod("testMovementValidDirectionHasMoved", JsonParameterSet.class))
                    )
                )
                .build()
        )
        .addChildCriteria(
            Criterion.builder()
                .shortDescription("H2 | Steuerung der \"Contaminant\"-Roboter")
                .addChildCriteria(
                    Criterion.builder()
                        .shortDescription("H2.1 | Contaminant1")
                        .addChildCriteria(
                            criterion(
                                "Der \"Contaminant1\"-Roboter legt korrekt eine zufällige Anzahl an Münzen ab, genau dann wenn er es soll."
                            ),
                            criterion(
                                "Der \"Contaminant1\"-Roboter prüft alle vier Richtungen ab und dreht sich wieder in die Ausgangsrichtung."
                            ),
                            criterion(
                                "Der \"Contaminant1\"-Roboter bewegt sich korrekt in eine zufällige Richtung, die frei ist, falls eine solche existiert."
                            ),
                            criterion(
                                "Die Bewegung des \"Contaminant1\"-Roboters ist vollständig korrekt."
                            )
                        )
                        .build(),
                    Criterion.builder()
                        .shortDescription("H2.2 | Contaminant2")
                        .addChildCriteria(
                            criterion(
                                "Der \"Contaminant2\"-Roboter legt stets die korrekte Anzahl an Münzen ab."
                            ),
                            criterion(
                                "Der \"Contaminant2\"-Roboter prüft alle vier Richtungen ab und dreht sich wieder in die Ausgangsrichtung."
                            ),
                            criterion(
                                "Der \"Contaminant2\"-Roboter bewegt sich korrekt nach dem Bewegungsschema."
                            ),
                            criterion(
                                "Die Bewegung des \"Contaminant2\"-Roboters ist vollständig korrekt."
                            )
                        )
                        .build()
                )
                .build(),
            Criterion.builder()
                .shortDescription("H3 | Gewinnbedingungen")
                .addChildCriteria(
                    criterion(
                        "Der cleaner gewinnt, wenn alle Contaminants ausgeschaltet sind."
                    ),criterion(
                        "Der cleaner gewinnt, wenn sich in der Abladezone mindestens 200 Münzen befinden."
                    ),
                    criterion(
                        "Die Contaminants gewinnen, wenn mindestens 50% der Felder mit münzen bedeckt sind."
                    ),
                    criterion(
                        "Wenn beide Parteien gleichzeitig gewinnen, so gewinnt der cleaner."
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
