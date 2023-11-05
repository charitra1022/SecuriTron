module com.soterians.securitron {
  requires javafx.controls;
  requires javafx.fxml;


  opens com.soterians.securitron to javafx.fxml;
  exports com.soterians.securitron;
}