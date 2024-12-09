package com.ca.chatappcs4.Interfaces;

import com.ca.chatappcs4.Model.User;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXSnackbar;
import com.jfoenix.controls.JFXTextField;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconName;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.controlsfx.validation.ValidationSupport;
import org.controlsfx.validation.Validator;

import javax.swing.*;
import java.io.*;
import java.net.*;
import java.util.Enumeration;

public class signUpController
{

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField emailTextField;

    @FXML
    private PasswordField passwordTextField;

    @FXML
    private TextField textPasswordField;

    @FXML
    private CheckBox showPassCheckBox;
    @FXML
    private StackPane rootStackPane;

    private TextField portTextField;
    JFXSnackbar snackbar ;

    public InetAddress getIpAdresse() throws SocketException
    {
        Enumeration en = NetworkInterface.getNetworkInterfaces() ;
        while (en.hasMoreElements())
        {
            NetworkInterface i = (NetworkInterface) en.nextElement();
            for (Enumeration en2 = i.getInetAddresses();en2.hasMoreElements();)
            {
                InetAddress addr = (InetAddress) en2.nextElement();
                if (!addr.isLoopbackAddress())
                {
                    if (addr instanceof Inet4Address)
                    {
                        return addr ;
                    }
                }
            }
        }
        return null ;
    }
    @FXML
    void SignUp()
    {
        ValidationSupport validationSupport = new ValidationSupport();
        validationSupport.registerValidator(usernameTextField, Validator.createEmptyValidator("Username is Required")) ;
        validationSupport.registerValidator(emailTextField, Validator.createEmptyValidator("Email is Required")) ;
        validationSupport.registerValidator(passwordTextField, Validator.createEmptyValidator("password is Required")) ;


        if (!usernameTextField.getText().isEmpty()&
                !passwordTextField.getText().isEmpty()&
                !emailTextField.getText().isEmpty() )
        {
            try
            {

                Socket s =new Socket("localhost" ,9191) ;

                InputStream is =s.getInputStream();
                InputStreamReader isr =new InputStreamReader(is) ;
                BufferedReader bf =new BufferedReader(isr) ;
                ObjectInputStream ois = new ObjectInputStream(is);

                OutputStream os =s.getOutputStream() ;
                PrintWriter pw =new PrintWriter(os,true) ;
                ObjectOutputStream oos = new ObjectOutputStream(os);


                /*------------------Creat New User ---------------*/
                String username =usernameTextField.getText() ;
                String email =emailTextField.getText() ;
                String password =passwordTextField.getText();
                String ip = getIpAdresse().getHostAddress();
                int port = Integer.parseInt(portTextField.getText());
                /*------------------------------------------------------*/

                User u = new User(username,email,password,ip,port,"Offline") ;

                //Read id
                bf.readLine();

                // <<1>> means that this is a registration request ==> For the server
                pw.println(1);

                //then we send the user  information to the server to add it in the data base
                oos.writeObject(u);

                //Get the reponse from server
                String reponse = bf.readLine();
                if (reponse.equals("false"))
                {
                    showToast();
                }
                else
                {
                    JOptionPane.showMessageDialog(null,"Username  exists, try another one !",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            catch (IOException e)
            {
                System.out.println(e.getMessage());
            }
        }
    }

    /*---------------------------------------------------------------------------------*/
    public void showPassword()
    {
        if (showPassCheckBox.isSelected()) {
            textPasswordField.setText(passwordTextField.getText());
            textPasswordField.setVisible(true);
            passwordTextField.setVisible(false);
        } else {
            passwordTextField.setText(textPasswordField.getText());
            passwordTextField.setVisible(true);
            textPasswordField.setVisible(false);
        }

    }

    /*---------------------------------------------------------------------------------------------------*/
    @FXML
    void Setting()
    {
        TextInputDialog textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("Setting");

        Pane pane = new Pane() ;
        pane.setPrefSize(350,100);

        portTextField = new TextField() ;
        portTextField.setPromptText("Port");
        portTextField.setLayoutX(65);
        portTextField.setLayoutY(50);
        Label l1 = new Label("Port :") ;
        l1.setLayoutX(20);
        l1.setLayoutY(54);
        portTextField.setPrefSize(250,20);

        pane.getChildren().addAll(portTextField,l1) ;
        textInputDialog.setHeaderText(null);
        textInputDialog.getDialogPane().setContent(pane);
        textInputDialog.showAndWait() ;
    }
    /*----------------------------------------------------------------------------------------------------------*/
    public void showToast()
    {
        snackbar = new JFXSnackbar();
        Text download = GlyphsDude.createIcon(FontAwesomeIconName.CHECK_CIRCLE,"2em") ;
        download.setFill(Color.valueOf("#ffffff"));
        snackbar = new JFXSnackbar(rootStackPane) ;
        snackbar.fireEvent( new JFXSnackbar.SnackbarEvent(download));
    }

    public void Clear(ActionEvent actionEvent) {
        usernameTextField.clear();
        emailTextField.clear();
        passwordTextField.clear();
        textPasswordField.clear();
    }

    public void Back(ActionEvent actionEvent) {
        try {
            // Tải FXML của màn hình đăng ký
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ca/chatappcs4/FXML/DangNhap.fxml"));
            Parent signupRoot = loader.load();

            // Lấy cửa sổ hiện tại (Stage)
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            // Cài đặt Scene mới cho cửa sổ
            Scene scene = new Scene(signupRoot);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
