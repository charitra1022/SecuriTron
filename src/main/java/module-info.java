module com.soterians.securitron {
  requires javafx.controls;
  requires javafx.fxml;
  requires org.json;
  requires AnimateFX;
  requires org.apache.commons.io;


  opens com.soterians.securitron to javafx.fxml;
  exports com.soterians.securitron;
  exports com.soterians.securitron.UI;
  opens com.soterians.securitron.UI to javafx.fxml;
  exports com.soterians.securitron.Utils;
  opens com.soterians.securitron.Utils to javafx.fxml;
  exports com.soterians.securitron.Utils.CryptoClasses;
  opens com.soterians.securitron.Utils.CryptoClasses to javafx.fxml;
}