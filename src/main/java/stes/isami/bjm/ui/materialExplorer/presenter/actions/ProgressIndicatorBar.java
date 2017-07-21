package stes.isami.bjm.ui.materialExplorer.presenter.actions;

import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * Created by cosmin2 on 03/07/2017.
 */
class ProgressIndicatorBar extends StackPane {
    final private ReadOnlyDoubleProperty workDone;
    final private SimpleStringProperty textProperty;
    final private double totalWork;

    final private ProgressBar bar  = new ProgressBar();
    private Text text = new Text();

    final private static int DEFAULT_LABEL_PADDING = 5;

    ProgressIndicatorBar(final ReadOnlyDoubleProperty workDone, final double totalWork, final SimpleStringProperty textProperty) {
        this.workDone  = workDone;
        this.totalWork = totalWork;
        this.textProperty = textProperty;
        text.setText(textProperty.get());

        syncProgress();
        workDone.addListener(new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                syncProgress();
            }
        });
        textProperty.addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                syncProgress();
            }
        });

        bar.setMaxWidth(Double.MAX_VALUE); // allows the progress bar to expand to fill available horizontal space.

        getChildren().setAll(bar, text);
    }

    // synchronizes the progress indicated with the work done.
    private void syncProgress() {
        if (workDone == null || totalWork == 0) {
             bar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        } else {
             bar.setProgress(workDone.get() / totalWork);
        }
        text.setText(textProperty.get());
        bar.setMinHeight(text.getBoundsInLocal().getHeight() + DEFAULT_LABEL_PADDING * 2);
        bar.setMinWidth (text.getBoundsInLocal().getWidth()  + DEFAULT_LABEL_PADDING * 2);
    }
}
