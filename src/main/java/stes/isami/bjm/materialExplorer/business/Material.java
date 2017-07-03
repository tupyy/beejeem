package stes.isami.bjm.materialExplorer.business;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Created by cosmin2 on 02/07/2017.
 */
public class Material {

    private final SimpleBooleanProperty selectedProperty;
    private final SimpleStringProperty libraryName;
    private final SimpleStringProperty materialName;
    private final SimpleStringProperty referenceName;

    public Material(String libraryName, String materialName, String referenceName) {
        this.selectedProperty = new SimpleBooleanProperty(false);
        this.libraryName = new SimpleStringProperty(libraryName);
        this.materialName = new SimpleStringProperty(materialName);
        this.referenceName = new SimpleStringProperty(referenceName);
    }


    public Boolean isSelected() {
        return selectedProperty.getValue();
    }

    public void setSelected(boolean value) {
        selectedProperty.setValue(value);
    }

    public SimpleBooleanProperty getSelectedProperty() {
        return selectedProperty;
    }

    public String getLibrary() {
        return libraryName.getValue();
    }

    public String getMaterialName() {
        return materialName.getValue();
    }

    public String getReferenceName() {
        return referenceName.getValue();
    }
}
