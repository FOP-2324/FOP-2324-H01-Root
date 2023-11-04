package h01;

import org.sourcegrade.jagr.api.rubric.Criterion;
import org.sourcegrade.jagr.api.rubric.JUnitTestRef;
import org.sourcegrade.jagr.api.rubric.Rubric;
import org.sourcegrade.jagr.api.rubric.RubricProvider;
import org.sourcegrade.jagr.api.testing.RubricConfiguration;
import org.tudalgo.algoutils.transform.AccessTransformer;
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
                        JUnitTestRef.ofMethod(() -> CleaningRobotTest.class.getDeclaredMethod(
                            "testMovementInvalidDirection",
                            JsonParameterSet.class
                        ))
                    ),
                    criterion(
                        "Wenn die übergebene Direction >= 0 und < 4 ist, dann schaut der Roboter in die entsprechende Richtung.",
                        JUnitTestRef.ofMethod(() -> CleaningRobotTest.class.getDeclaredMethod(
                            "testMovementValidDirectionRotation",
                            JsonParameterSet.class
                        ))
                    ),
                    criterion(
                        "Wenn die übergebene Direction >= 0 und < 4 ist, so bewegt sich der Roboter in die entsprechende Richtung, falls der Weg frei ist.",
                        JUnitTestRef.ofMethod(() -> CleaningRobotTest.class.getDeclaredMethod(
                            "testMovementValidDirectionHasMoved",
                            JsonParameterSet.class
                        ))
                    ),
                    criterion(
                        "shouldPutCoin und shouldPickCoin werden korrekt verarbeitet.",
                        JUnitTestRef.ofMethod(() -> CleaningRobotTest.class.getDeclaredMethod(
                            "testMovementCoins",
                            JsonParameterSet.class
                        ))
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
                                "Wenn der \"Contaminant1\"-Roboter keine Münzen mehr hat, wird er ausgeschaltet und die Methode doMove() führt keine weiteren Aktionen aus.",
                                JUnitTestRef.ofMethod(() -> Contaminant1Test.class.getDeclaredMethod(
                                    "testTurnOff",
                                    JsonParameterSet.class
                                ))
                            ),
                            criterion(
                                "Der \"Contaminant1\"-Roboter legt korrekt eine zufällige Anzahl an Münzen ab, genau dann wenn er es soll.",
                                JUnitTestRef.ofMethod(() -> Contaminant1Test.class.getDeclaredMethod(
                                    "testCoins",
                                    JsonParameterSet.class
                                ))
                            ),
                            criterion(
                                "Der \"Contaminant1\"-Roboter prüft alle vier Richtungen ab und dreht sich wieder in die Ausgangsrichtung.",
                                JUnitTestRef.ofMethod(() -> Contaminant1Test.class.getDeclaredMethod(
                                    "testRotation",
                                    JsonParameterSet.class
                                ))
                            ),
                            criterion(
                                "Der \"Contaminant1\"-Roboter bewegt sich korrekt in eine zufällige Richtung, die frei ist, falls eine solche existiert.",
                                JUnitTestRef.ofMethod(() -> Contaminant1Test.class.getDeclaredMethod(
                                    "testTheMovement",
                                    JsonParameterSet.class
                                ))
                            )
                        )
                        .build(),
                    Criterion.builder()
                        .shortDescription("H2.2 | Contaminant2")
                        .addChildCriteria(
                            criterion(
                                "Wenn der \"Contaminant2\"-Roboter keine Münzen mehr hat, wird er ausgeschaltet und die Methode doMove() führt keine weiteren Aktionen aus.",
                                JUnitTestRef.ofMethod(() -> Contaminant2Test.class.getDeclaredMethod(
                                    "testTurnOff",
                                    JsonParameterSet.class
                                ))
                            ),
                            criterion(
                                "Der \"Contaminant2\"-Roboter legt stets die korrekte Anzahl an Münzen ab.",
                                JUnitTestRef.ofMethod(() -> Contaminant2Test.class.getDeclaredMethod(
                                    "testCoins",
                                    JsonParameterSet.class
                                ))
                            ),
                            criterion(
                                "Der \"Contaminant2\"-Roboter prüft alle vier Richtungen ab und dreht sich wieder in die Ausgangsrichtung.",
                                JUnitTestRef.ofMethod(() -> Contaminant2Test.class.getDeclaredMethod(
                                    "testRotation",
                                    JsonParameterSet.class
                                ))
                            ),
                            criterion(
                                "Der \"Contaminant2\"-Roboter bewegt sich korrekt nach dem Bewegungsschema.",
                                JUnitTestRef.ofMethod(() -> Contaminant2Test.class.getDeclaredMethod(
                                    "testTheMovement",
                                    JsonParameterSet.class
                                ))
                            )
                        )
                        .build()
                )
                .build(),
            Criterion.builder()
                .shortDescription("H3 | Gewinnbedingungen")
                .addChildCriteria(
                    criterion(
                        "Der cleaner gewinnt, wenn alle Contaminants ausgeschaltet sind.",
                        JUnitTestRef.ofMethod(
                            () -> GameControllerTest.class.getDeclaredMethod(
                                "testCleaningRobotWinByEndurance",
                                boolean.class,
                                boolean.class
                            ))
                    ),
                    criterion(
                        "Der cleaner gewinnt, wenn sich in der Abladezone mindestens 200 Münzen befinden.",
                        JUnitTestRef.ofMethod(
                            () -> GameControllerTest.class.getDeclaredMethod(
                                "testCleaningRobotWinByDumpingArea",
                                int.class
                            ))
                    ),
                    criterion(
                        "Die Contaminants gewinnen, wenn mindestens 50% der Felder mit münzen bedeckt sind.",
                        JUnitTestRef.ofMethod(
                            () -> GameControllerTest.class.getDeclaredMethod(
                                "testContaminantsWin",
                                int.class,
                                int.class
                            ))
                    ),
                    criterion(
                        "Wenn beide Parteien gleichzeitig gewinnen, so gewinnt der cleaner.",
                        JUnitTestRef.ofMethod(
                            () -> GameControllerTest.class.getDeclaredMethod(
                                "testContaminantsWin", int.class, int.class))
                    )
                )
                .build()
        )
        .build();

    @Override
    public Rubric getRubric() {
        return RUBRIC;
    }

    @Override
    public void configure(final RubricConfiguration configuration) {
        configuration.addTransformer(new AccessTransformer());
    }
}
