package com.ca.chatappcs4.Interfaces;

import com.ca.chatappcs4.Model.Ressource;
import com.ca.chatappcs4.Model.User;
import com.ca.chatappcs4.Model.file;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.GlyphsDude;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIcon;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconName;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Vector;


public class MainController  implements Initializable
{
    public static Socket connectionSocket;

    public static ObjectOutputStream oos;
    public static ObjectInputStream ois;
    public static BufferedReader bis;
    public static PrintWriter pw;
    public static String username;
    public static int MyPort ;
    public static int portUser2 ;
    public static String ipUser2 ;
    public  static String NameFile ;
//    URL src = getClass().getResource("messageNotif.mp3") ;
//    AudioClip clipMessageNotif = new AudioClip(src.toString()) ;
//    URL src2 =getClass().getResource("request.mp3") ;
//    AudioClip clipRequestNotif = new AudioClip(src2.toString()) ;

    /*-------FXML component---------*/
    @FXML
    private Label userFentre;
    public StackPane rootStackPane;
    public AnchorPane rootAnchorPane;


    /*-------------function to retrieve information from the login interface--------------------------*/

    public void getInfoConnexion(Socket socket, String username, ObjectOutputStream oos, ObjectInputStream ois)
    {
        try
        {
            connectionSocket = socket;
            MainController.username = username;

            //Input Stream
            InputStream is = connectionSocket.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            bis = new BufferedReader(isr);//Buffer
            MainController.ois = ois;//Object

            //Output Stream
            OutputStream os = connectionSocket.getOutputStream();
            pw = new PrintWriter(os, true);//Buffer ==> true :autoFlush
            MainController.oos = oos;//Object

        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
    public void  setStatistque()
    {
//        Vector<file> files = count();
//        XYChart.Series set1 = new XYChart.Series<>();
//        for (int i=0;i<files.size();i++)
//        {
//            set1.getData().add(new XYChart.Data<>(files.get(i).getFile(),files.get(i).getCount())) ;
//        }
//        barChartFile.getData().clear() ;
//        barChartFile.getData().addAll(set1) ;

        nombreUser.setText(countUsers()+" Peer");
        // nombreRess.setText(countRess()+" Resource");
    }
    /*--------------------------------------------------------------------------------**/
    @FXML
    void About() {
        BoxBlur boxBlur = new BoxBlur(5, 5, 3);
        JFXButton Okey = new JFXButton("OK");
        Okey.getStyleClass().add("dialogButtonYes");
        JFXDialogLayout layout = new JFXDialogLayout();
        ImageView imageView = new ImageView(new Image(getClass().getResource("/com/ca/chatappcs4/image/Logo.png").toExternalForm()));
        imageView.setFitWidth(70);
        imageView.setFitHeight(70);
        imageView.setPreserveRatio(true);
        VBox contentBox = new VBox(10);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.getChildren().addAll(
                new Text("MEET THE TEAM"),
                new Text("Developed by:\nNguyen Anh Thu")
        );
        contentBox.getStyleClass().add("dialogContent");
        layout.setHeading(imageView);
        layout.setBody(contentBox);
        layout.setActions(Okey);
        JFXDialog dialog = new JFXDialog(rootStackPane, layout, JFXDialog.DialogTransition.TOP);
        dialog.show();
        rootAnchorPane.setEffect(boxBlur);
        Okey.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseEvent) -> {
            rootAnchorPane.setEffect(null); // Gỡ bỏ hiệu ứng mờ
            dialog.close();
        });
    }

    /*--------------Initialize method (execute when starting the application)-------------------------*/
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        userFentre.setText(username);
        //  homeIcon.setFill(Paint.valueOf("#151928"));
        // homeBtn.getStyleClass().add("btn");

        // AudioCallBtn.setDisable(true);
        vedioCallBtn.setDisable(true);


        /*----------------For Chat Pen-----------------*/
        listView.addAll(getListUser());
        listView.remove(username);
        list.setItems(listView);
        list.setCellFactory(param -> new Cell());

        /*---------------------------For Share Files Pen ------------------------*/
        fileName.setCellValueFactory(new PropertyValueFactory<>("fileName"));
        peerName.setCellValueFactory(new PropertyValueFactory<>("peerName"));
        ipAddress.setCellValueFactory(new PropertyValueFactory<>("ip"));
        numPort.setCellValueFactory(new PropertyValueFactory<>("port"));
        statusPeer.setCellValueFactory(new PropertyValueFactory<>("Status"));
        pathFile.setCellValueFactory(new PropertyValueFactory<>("pathFile"));
        Update();//update list of resource


        /*---------------------------For Home Pen ---------------------------------*/
        setStatistque();


        /*------------------Thread to make client also server----------------------------*/
        Thread WaitConnection = new Thread(() ->
        {
            try
            {
                MyPort = getMyPortNumber(username) ;
                ServerSocket ss = new ServerSocket(MyPort);
                while (true)
                {
                    System.out.println("Starting Server ......");
                    Socket s = ss.accept();
                    new conversation(s).start();
                }
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });
        WaitConnection.start();
    }
    /*-----------------------------------------------------------------------------------*/
    public class conversation extends Thread
    {
        Socket chat ;

        public conversation(Socket chat)
        {
            this.chat = chat;
        }

        @Override
        public void run()
        {
            byte[] bytes = new byte[16 * 1024];

            try
            {

                OutputStream outputStream = chat.getOutputStream();
                PrintWriter pw = new PrintWriter(outputStream, true);
                ObjectInputStream ois = new ObjectInputStream(chat.getInputStream()) ;


                //int request = Integer.parseInt(bf.readLine()); //read request of usr
                int request = (int) ois.readObject();
                System.out.println(request);

                switch (request)
                {
                    // 1=> means they want to download the file
                    case 1:
                        File f = (File) ois.readObject();
                        System.out.println(f.getAbsolutePath());
                        InputStream in = new FileInputStream(f);
                        int count;
                        while ((count = in.read(bytes)) > 0)
                        {
                            outputStream.write(bytes, 0, count);
                        }
                        chat.close();
                        break;

                    // 2=> means they want to chats with me
                    case 2:
                        BufferedReader bf2 = new BufferedReader(new InputStreamReader(chat.getInputStream()));
                        String contact = bf2.readLine() ;//Read the name of the person who wants to chat with me
                        portUser2 = getMyPortNumber(contact) ;
                        showOption(chat,bf2,contact);//showOption makes me choose if I want to accept or reject
                        String reponse = bf2.readLine() ;
                        if (reponse.equals("true"))
                        {
                            new chat(chat, bf2).run() ;
                        }
                        else
                        {
                            chat.close();
                        }
                        break;
                    // 3=> means he wants to talk to me on a video call
                    case 3:
                        new VedioCall(chat,ois).run() ;
                        break;
                    case 4:
                        new AudioCall(chat).run();
                        break;
                }
            }
            catch (IOException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }

        }
    }
    /*---------------------Quit l'app----------------------------*/
    @FXML
    void LogOut()
    {
        pw.println(5);
        pw.println(username);
        Platform.exit();
        System.exit(0);
    }
    /*---------------------Get My port number---------------------*/
    public int getMyPortNumber(String username)
    {
        int portNumb = 0;
        pw.println(6);
        pw.println(username);
        try
        {
            portNumb = Integer.parseInt(bis.readLine());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return portNumb;
    }





    /*******************************************************************************************************************
     *                                                                                                                 *
     *                                                   HOME PEN                                                      *
     *                                                                                                                 *
     ******************************************************************************************************************/

    /*-------FXML component---------*/
    @FXML
    //  public FontAwesomeIcon homeIcon;
    // public JFXButton homeBtn;
    // public Pane HomePane;
    public BarChart<?, ?> barChartFile;
    public Text nombreUser;
    // public Text nombreRess;
    /*-------------------------------*/

    /*-------------------------------List Of Function -----------------------------------------------*/
//    @FXML
//    void ShowHomePane()
//    {
//        HomePane.setVisible(true);
//        ShearPane.setVisible(false);
//        ChatsPane.setVisible(false);
//
//        homeIcon.setFill(Paint.valueOf("#151928"));
//        homeBtn.getStyleClass().removeAll("btn");
//        homeBtn.getStyleClass().add("btn") ;
//
//        shearicon.setFill(Paint.valueOf("#ffffff"));
//        shearBtn.getStyleClass().removeAll("btn");
//
//        imageMessanger.setImage(new Image(getClass().getResource("/com/ca/chatappcs4/image/messenger.png").toExternalForm()));
//        mssengerBtn.getStyleClass().removeAll ("btn-not");
//    }
    /*-------count the number of occurrences for each file-------*/
    public Vector<file> count()
    {
        Vector<file> files = new Vector<>();
        try
        {
            pw.println(11);

            files = (Vector<file>) ois.readObject();

        }
        catch (ClassNotFoundException | IOException e)
        {
            System.out.println(e.getMessage());
        }
        return files ;
    }
    /*-----Count the number of users in the network----------------*/
    public int countUsers()
    {
        int nbUser = 0;
        pw.println(12);
        try
        {
            nbUser =  Integer.parseInt(bis.readLine()) ;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return nbUser ;
    }
    /*-----Count the number of resource in the network------------*/
    public int countRess()
    {
        int nbRess = 0;
        pw.println(13);
        try
        {
            nbRess =  Integer.parseInt(bis.readLine()) ;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return nbRess ;
    }
    /*------------------------------------------------------------------*/
    @FXML
    void popover(MouseEvent event)
    {
    }

    /*******************************************************************************************************************
     *                                                                                                                 *
     *                                                   Share PEN                                                     *
     *                                                                                                                 *
     ******************************************************************************************************************/

    /*-------FXML component---------*/
    @FXML
    public JFXButton shearBtn;
    public FontAwesomeIcon shearicon;
    public Pane ShearPane;
    public JFXTextField SearchField;
    /*-------------------------------*/

    /*------------ TableView of resource------------*/
    public TableView<Ressource> tableF;
    public TableColumn<Ressource, String> fileName;
    public TableColumn<Ressource, String> peerName;
    public TableColumn<Ressource, String> ipAddress;
    public TableColumn<Ressource, Integer> numPort;
    public TableColumn<Ressource, String> statusPeer;
    public TableColumn<Ressource, String> pathFile;

    /*----------------Other Variables ------------*/
    private int indexF = -1;
    private TextInputDialog textInputDialog;
    private TextField filePath;
    public static File  file;
    private JFXSnackbar snackbar ;

    /*-------------------------------List Of Function -----------------------------------------------*/
    @FXML
    void ShowShearPane()
    {
        ShearPane.setVisible(true);
        //  HomePane.setVisible(false);
        ChatsPane.setVisible(false);

        shearicon.setFill(Paint.valueOf("#151928"));
        shearBtn.getStyleClass().removeAll() ;
        shearBtn.getStyleClass().add("btn") ;

        //   homeIcon.setFill(Paint.valueOf("#ffffff"));
        //  homeBtn.getStyleClass().removeAll("btn");

        imageMessanger.setImage(new Image(getClass().getResource("/com/ca/chatappcs4/image/Logo.png").toExternalForm()));
        mssengerBtn.getStyleClass().removeAll("btn-not");
    }
    @FXML
    void Refresh()
    {
        Update();
    }

    /*----------------------------------Search resource---------------------------------*/
    @FXML
    void SearchFile()
    {
        if (!SearchField.getText().isEmpty())
        {
            String filename = SearchField.getText();
            System.out.println(SearchField.getText());
            pw.println(3);//Send Option to server
            pw.println(filename);

            try
            {
                ArrayList<Ressource> list = new ArrayList<>();
                list.add((Ressource) ois.readObject());
                ObservableList<Ressource> res = FXCollections.observableArrayList(list);
                tableF.getItems().removeAll();
                tableF.setItems(res);
            }
            catch (IOException | ClassNotFoundException e)
            {
                System.out.println(e.getMessage());
            }
        }
    }
    /*----------------------------Share a resource------------------------------------*/
    @FXML
    void UploadFile() throws IOException {
        showDialog();
        Optional<String> result = textInputDialog.showAndWait();

        if (result.isPresent())
        {
            pw.println(4);
            oos.writeObject(file);//send file path
            pw.println(username);

            System.out.println(file);
            System.out.println(username);
        }
        Update();
        setStatistque();
    }
    /*-----------------------Delete resource---------------------------------------*/
    @FXML
    void DeleteFile()
    {
        indexF = tableF.getSelectionModel().getSelectedIndex();

        if (indexF <= -1)
        {
            return;
        }
        else
        {
            String name = fileName.getCellData(indexF);

            if (peerName.getCellData(indexF).equals(username))
            {
                pw.println(8);
                pw.println(name);
                pw.println(username);
                String reponse = null;
                try
                {
                    reponse = bis.readLine();
                    if (reponse.equals("false"))
                    {
                        JOptionPane.showMessageDialog(null, "The file was successfully deleted ",
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                    }
                    else
                    {
                        JOptionPane.showMessageDialog(null, "Something wrong , the file is not delete !",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                catch (IOException e)
                {
                    System.out.println(e.getMessage());
                }

            }
            else
            {
                JOptionPane.showMessageDialog(null, "You cannot delete this file," +
                                " you are not the owner !?",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        Update();
        setStatistque();
    }
    /*-----------------------Download resource---------------------------------------*/
    @FXML
    void downloadFile()
    {
        indexF = tableF.getSelectionModel().getSelectedIndex();
        if (indexF <= -1)
        {
            return;
        }
        else
        {
            String name = fileName.getCellData(indexF);
            System.out.println("aaa"+peerName.getCellData(indexF));
            String ip = ipAddress.getCellData(indexF);
            int port = numPort.getCellData(indexF);
            String file = pathFile.getCellData(indexF);
            System.out.println("aaa"+file);
            String status = statusPeer.getCellData(indexF);

            if (peerName.getCellData(indexF).equals(username))
            {
                JOptionPane.showMessageDialog(null, "You are the owner of the file, " +
                                "you do not need to download it from here !",
                        "Information", JOptionPane.INFORMATION_MESSAGE);
            }
            else
            {
                if (status.equals("Online"))
                {
                    try
                    {
                        JFileChooser fileChooser = new JFileChooser();
                        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        fileChooser.showSaveDialog(null);
                        String path = String.valueOf(fileChooser.getSelectedFile());
                        File f = new File(path + "/" + name);
                        System.out.println("Path where i save" + f.getAbsolutePath());
                        File filedownload = new File(file) ;


                        chatSocket = new Socket();
                        chatSocket.connect(new InetSocketAddress(ip, port), 5000);

                        byte[] bytes = new byte[16 * 1024];


                        PrintWriter printWriter = new PrintWriter(chatSocket.getOutputStream(), true);
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(chatSocket.getOutputStream()) ;

                        OutputStream out = new FileOutputStream(f);


                        objectOutputStream.writeObject(1); //send request code 1==> mean download file
                        //printWriter.println(file);//Send Path file to Peer server to download
                        objectOutputStream.writeObject(filedownload);

                        System.out.println("file to downnload" + filedownload.getAbsolutePath());
                        int count;
                        InputStream inputStream = chatSocket.getInputStream();
                        while ((count = inputStream.read(bytes)) > 0)
                        {
                            out.write(bytes, 0, count);
                        }
                        out.close();
                        chatSocket.close();
                        showToast();

                        //Add the resource to the "annuair"
                        pw.println(4);
                        oos.writeObject(f);
                        pw.println(username);

                    }
                    catch (IOException e)
                    {
                        System.out.println(e.getMessage());
                    }
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "You can't download the file Peer is offline !",
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

        }
        setStatistque();
    }
    /*----------------------update list of resource-----------------------------------*/
    public void Update()
    {
        try
        {
            pw.println(7);
            ArrayList<Ressource> list = (ArrayList<Ressource>) ois.readObject();
            ObservableList<Ressource> res = FXCollections.observableArrayList(list);
            tableF.setItems(res);
        }
        catch (ClassNotFoundException | IOException e)
        {
            System.out.println(e.getMessage());
        }
    }
    /*--------------------display the dialog box to choose the file path--------------*/
    public void showDialog()
    {
        textInputDialog = new TextInputDialog();
        textInputDialog.setTitle("Choose File");
        Pane pane = new Pane();
        pane.setPrefSize(350, 130);

        filePath = new TextField();
        filePath.setPrefSize(250, 20);
        filePath.setPromptText("Path File :");
        filePath.setLayoutX(20);
        filePath.setLayoutY(50);


        JFXButton Browse = new JFXButton("Browse");
        Browse.setStyle(" -fx-background-color :#ffffff");
        Browse.setLayoutX(280);
        Browse.setLayoutY(50);

        Browse.setButtonType(JFXButton.ButtonType.RAISED);

        pane.getChildren().addAll(filePath, Browse);
        textInputDialog.setHeaderText(null);
        textInputDialog.getDialogPane().setContent(pane);
        Browse.setOnMouseClicked(event ->
        {
            FileChooser fileChooser = new FileChooser();
            file = fileChooser.showOpenDialog(rootAnchorPane.getScene().getWindow());
            filePath.setText(file.getAbsolutePath());
        });
    }
    public void showToast()
    {
        snackbar = new JFXSnackbar();
        Text download = new Text("File downloaded successfully ");
        download.setFill(Color.valueOf("#ffffff"));
        snackbar = new JFXSnackbar(rootStackPane) ;
        snackbar.fireEvent( new JFXSnackbar.SnackbarEvent(download));
    }


    /*******************************************************************************************************************
     *                                                                                                                 *
     *                                                   CHAT PEN                                                      *
     *                                                                                                                 *
     ******************************************************************************************************************/

    /*---------------------------------------------------------------*/
    /*                      Instant messaging                        */
    /*---------------------------------------------------------------*/

    /*-------FXML component---------*/
    @FXML
    public ImageView imageMessanger;
    public Pane ChatsPane;
    public JFXButton mssengerBtn;
    public VBox espaceMsg;
    public Label message;
    public JFXButton sendButton;
    public TextField textFielMessage;
    // public Label usernameLabel;
    public JFXListView<String> list;
    public ImageView vectorImage;



    ObservableList<String> listView = FXCollections.observableArrayList();
    public static Socket chatSocket ;

    /*-------------------------------List Of Function -----------------------------------------------*/
    @FXML
    void ShowChatsPane()
    {
        ChatsPane.setVisible(true);
        //     HomePane.setVisible(false);
        ShearPane.setVisible(false);

        imageMessanger.setImage(new Image(getClass().getResource("/com/ca/chatappcs4/image/messengerd.png").toExternalForm()));
        mssengerBtn.getStyleClass().removeAll("btn") ;
        mssengerBtn.getStyleClass().add("btn-not") ;

        // homeIcon.setFill(Paint.valueOf("#ffffff"));
        //  homeBtn.getStyleClass().removeAll("btn");

        shearicon.setFill(Paint.valueOf("#ffffff"));
        shearBtn.getStyleClass().removeAll("btn");
    }
    /*-------------------------------Quit Chat ---------------------------*/
//    @FXML
//    void quitChat(ActionEvent event)
//    {
//
//    }
    /*-----------------Get list of users from server-------------------*/
    public ObservableList<String> getListUser()
    {
        ObservableList<String> list = null;
        pw.println(9);
        try
        {
            ArrayList<String> s = (ArrayList<String>) ois.readObject();
            list = FXCollections.observableArrayList(s);

        }
        catch (IOException | ClassNotFoundException e)
        {
            System.out.println(e.getMessage());
        }
        return list;
    }
    /*------------------------List of Users----------------------------*/
    public class Cell extends ListCell<String>
    {
        private JFXButton actionBtn;
        private Label name ;
        private GridPane pane ;
        private Image profile ;
        private ImageView img ;
        private Pane pane1;

        public Cell()
        {
            super();

            setOnMouseClicked(event ->
            {
                //do something
            });

            actionBtn = new JFXButton();
            Text download = GlyphsDude.createIcon(FontAwesomeIconName.USER_PLUS,"1.5em") ;
            download.setFill(Color.valueOf("#2196f3"));
            actionBtn.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT);
            actionBtn.setGraphic(download);
            actionBtn.setTextFill(Paint.valueOf("#ffffff"));
            actionBtn.setOnAction(event -> getCellUserName(getItem()));
            pane1 = new Pane() ;
            profile= new Image(getClass().getResource("/com/ca/chatappcs4/image/userdark.png").toExternalForm()) ;
            img = new ImageView(profile) ;
            img.setFitHeight(40);
            img.setFitWidth(40);
            name = new Label();
            name.getStyleClass().add("nameLabel") ;
            pane = new GridPane();
            pane.add(name, 1, 0);
            pane.add(pane1,2,0);
            pane.add(actionBtn, 3, 0);
            pane.add(img,0,0);
            GridPane.setHgrow(pane1,Priority.ALWAYS);
            setText(null);
        }

        @Override
        public void updateItem(String item, boolean empty)
        {
            super.updateItem(item, empty);
            setEditable(false);
            if (item != null)
            {
                name.setText(item);
                setGraphic(pane);
            }
            else
            {
                setGraphic(null);
            }
        }
    }
    /*-----------------------------Contact Someone---------------------*/
    public void getCellUserName(String user)
    {
        String contact = user;
        pw.println(10);
        pw.println(contact);
        try
        {
            User u = (User) ois.readObject();
            if (u.getStatus().equals("Online"))
            {
                portUser2 = u.getPort() ;
                ipUser2 = u.getIp() ;
                chatSocket = new Socket(ipUser2,portUser2);
                InputStream inputStream = chatSocket.getInputStream();
                OutputStream outputStream = chatSocket.getOutputStream() ;
                ObjectOutputStream oos = new ObjectOutputStream(outputStream) ;


                BufferedReader bf = new BufferedReader(new InputStreamReader(inputStream));
                PrintWriter pw = new PrintWriter(outputStream, true);


                oos.writeObject(2);
                pw.println(username);

                String reponse = bf.readLine();
                pw.println(reponse);


                System.out.println(reponse);


                if (reponse.equals("true"))
                {
                    // AudioCallBtn.setDisable(false);
                    vedioCallBtn.setDisable(false);
                    vectorImage.setVisible(false);
                    message.setTextFill(Paint.valueOf("#4af225"));
                    message.setText("You have now joined the conversation with " + contact + " !" +
                            " \n Enjoy chatting !");
                    textFielMessage.setEditable(true);
                    // usernameLabel.setText(contact);

                    /*-------------------Recive Thread -----------------------*/
                    Thread recive = new Thread(() ->
                    {
                        while (true)
                        {
                            try
                            {
                                String ms = bf.readLine();
//                                clipMessageNotif.play();
                                addNodeFxMethodLeft(ms);

                            }
                            catch (IOException e)
                            {
                                System.out.println(e.getMessage() );
                            }
                        }
                    });
                    recive.start();


                    /*---------------------------Sending option-------------------*/
                    sendButton.setOnAction(event ->
                    {
                        if (!textFielMessage.getText().isEmpty() )
                        {
                            String msg = textFielMessage.getText();
                            textFielMessage.clear();
                            pw.println(msg);
                            addNodeFxMethodRight(msg);
                        }

                    });
                }
                else
                {
                    JOptionPane.showMessageDialog(null, "Your request has been rejected !",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    chatSocket.close();
                }
            }
            else
            {
                JOptionPane.showMessageDialog(null,"User is offline !",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }




            /*---------------------------------------------------------------------------------*/
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
    /*--------------------Accept or refuse request---------------------*/
    public void showOption(Socket s , BufferedReader bof,String contact) throws IOException
    {
//        clipRequestNotif.play();
        PrintWriter printWriter = new PrintWriter(s.getOutputStream(),true) ;
        Platform.runLater(() ->
        {

            BoxBlur boxBlur = new BoxBlur(3, 3, 3);
            JFXButton no = new JFXButton("Refuse");
            JFXButton yes = new JFXButton("Accept");
            JFXDialogLayout layout = new JFXDialogLayout();
            no.getStyleClass().add("dialogButtonNo");
            yes.getStyleClass().add("dialogButtonYes");
            layout.setActions(no, yes);
            layout.setHeading(new Text(contact+" wants to chat with you?"));
            JFXDialog dialog = new JFXDialog(rootStackPane, layout, JFXDialog.DialogTransition.TOP);
            dialog.show();
            rootAnchorPane.setEffect(boxBlur);

            no.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseevnt) ->
            {
                rootAnchorPane.setEffect(null);
                dialog.close();
                printWriter.println("false");

            });
            yes.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent mouseevnt) ->
            {
                rootAnchorPane.setEffect(null);
                dialog.close();
                printWriter.println("true");
                addMessages(contact) ;
            });
        });

    }
    /*-----------------------------------------------------------------*/
    public void addNodeFxMethodLeft(String ms)
    {
        Platform.runLater(() ->
        {
            HBox h = new HBox();
            h.setSpacing(5);
            h.setAlignment(Pos.BASELINE_LEFT);
            Label l = new Label(ms);
            l.setMaxSize(400,400);
            l.getStyleClass().add("left");
            ImageView imageView = new ImageView(new Image(getClass().getResource("/com/ca/chatappcs4/image/userwhit.png").toExternalForm())) ;
            imageView.setFitWidth(30);
            imageView.setFitHeight(30);
            h.getChildren().addAll(imageView,l);
            espaceMsg.getChildren().add(h);
        });
    }

    public void addNodeFxMethodRight(String ms)
    {
        Platform.runLater(() ->
        {
            HBox h = new HBox();
            h.setSpacing(5);

            h.setAlignment(Pos.BASELINE_RIGHT);
            Label l = new Label(ms);
            l.setMaxSize(400,400);
            l.getStyleClass().add("right");
            ImageView imageView = new ImageView(new Image(getClass().getResource("/com/ca/chatappcs4/image/userdark.png").toExternalForm())) ;
            imageView.setFitWidth(30);
            imageView.setFitHeight(30);
            imageView.getStyleClass().add("imageright") ;
            h.getChildren().addAll(l,imageView);
            espaceMsg.getChildren().add(h);
        });
    }
    public void addMessages(String contact)
    {
        Platform.runLater(() ->
        {
            message.setTextFill(Paint.valueOf("#4af225"));
            message.setText("You have now joined the conversation with " + contact + " !" +
                    " \n Enjoy chatting !");
            textFielMessage.setEditable(true);
            // usernameLabel.setText(contact);
        });
    }
    /*----------------------Chat Class ----------------------------------*/
    public class chat implements Runnable
    {
        Socket s;
        OutputStream os;
        BufferedReader bf;
        PrintWriter pw;

        /*--------------------Constructor -------------------------*/
        public chat(Socket s, BufferedReader bf) throws IOException
        {
            this.s = s;
            os = s.getOutputStream();
            this.bf = bf;
            pw = new PrintWriter(os, true);
        }

        @Override
        public void run()
        {
            //  AudioCallBtn.setDisable(false);
            vedioCallBtn.setDisable(false);
            vectorImage.setVisible(false);
            chatSocket = s ;
            /**********************Sending Thread*********************/

            Thread send = new Thread(() ->
                    sendButton.setOnAction(event ->
                    {
                        if (!textFielMessage.getText().isEmpty()  )
                        {
                            String msg = textFielMessage.getText();
                            textFielMessage.clear();
                            pw.println(msg);
                            addNodeFxMethodRight(msg);
                        }
                    }));
            send.start();
            /*************************Reciving Boucle******************/
            while (true)
            {
                try
                {
                    String ms = bf.readLine();
//                    clipMessageNotif.play();
                    addNodeFxMethodLeft(ms);
                }
                catch (IOException e)
                {
                    System.out.println(e.getMessage());
                }
            }

        }
    }


    /*******************************************************************************************************************
     *                                                                                                                 *
     *                                                   Video Call                                                    *
     *                                                                                                                 *
     ******************************************************************************************************************/

    @FXML
    private Button vedioCallBtn;
    ImageView myimage ;
    ImageView user2 ;
    JFXButton btnEnd , audioCallBtn ;

    @FXML
    void VedioCall(ActionEvent event) throws IOException
    {
        String ip = chatSocket.getInetAddress().getHostAddress();
        int port = portUser2;
        System.out.println("IP "+ip + "Port "+port);

        Socket socket = new Socket(ip,port);

        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());

        oos.writeObject(3);
        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

        VedioCalling =true ;

        Stage primaryStage = new Stage();
        Pane pane = new Pane();
        pane.setPrefHeight(400.0);
        pane.setPrefWidth(858.0);
        pane.setStyle("-fx-background-color: #F0F0F0; -fx-background-radius: 15px;");

        myimage = new ImageView();
        myimage.setX(50);
        myimage.setY(60);
        myimage.setFitWidth(350);
        myimage.setFitHeight(200);
        myimage.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 5px; -fx-background-radius: 15px;");

        user2 = new ImageView();
        user2.setX(450);
        user2.setY(60);
        user2.setFitWidth(350);
        user2.setFitHeight(200);
        user2.setStyle("-fx-border-color: #FF9800; -fx-border-width: 5px; -fx-background-radius: 15px;");

        Label timeLabel = new Label();
        timeLabel.setText("Thời gian: " + java.time.LocalTime.now().withSecond(0).withNano(0)); // Lấy thời gian hiện tại
        timeLabel.setStyle("-fx-text-fill: black;");
        timeLabel.setLayoutX(50);
        timeLabel.setLayoutY(300);

        Text endCall = GlyphsDude.createIcon(FontAwesomeIconName.PHONE, "2em");
        endCall.setFill(Color.WHITE);
        btnEnd = new JFXButton();
        btnEnd.setStyle("-fx-background-color: #E9505B; -fx-background-radius: 20px");
        btnEnd.setPrefSize(50, 50);
        btnEnd.setGraphic(endCall);
        btnEnd.setLayoutX(350);
        btnEnd.setLayoutY(300);


        Text micSlash = GlyphsDude.createIcon(FontAwesomeIconName.MICROPHONE_SLASH, "2em");
        micSlash.setFill(Color.WHITE);
        audioCallBtn = new JFXButton();
        audioCallBtn.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 20px; -fx-text-fill: white;");
        audioCallBtn.setPrefSize(50, 50);
        audioCallBtn.setGraphic(micSlash);
        audioCallBtn.setLayoutX(450);  // Canh giữa nút (đặt vị trí theo chiều ngang)
        audioCallBtn.setLayoutY(300);  // Đặt vị trí gần dưới cùng


        AudioCall(new ActionEvent() , audioCallBtn );


// Thêm các phần tử vào pane
        pane.getChildren().addAll(myimage, user2, btnEnd, audioCallBtn, timeLabel);

// Đưa pane vào scene và hiển thị
        Scene scene = new Scene(pane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Video Meeting");
        primaryStage.show();

        final Dimension size = WebcamResolution.QVGA.getSize();
        Webcam webcam = Webcam.getDefault();
        webcam.setViewSize(size);
        webcam.open();

        btnEnd.setOnMouseClicked(e ->
        {
            VedioCalling=false ;
            primaryStage.close();
            webcam.close() ;
        });


        Thread sendVideo = new Thread(()->
        {
            BufferedImage bufferedImage;
            while (VedioCalling)
            {
                ImageIcon ic;
                bufferedImage = webcam.getImage();
                Image picture = SwingFXUtils.toFXImage(bufferedImage,null);
                myimage.setImage(picture);
                ic = new ImageIcon(bufferedImage);
                try
                {
                    oos.writeObject(ic);
                }
                catch (IOException e)
                {
                    VedioCalling = false ;
                }
            }
            try {
                socket.close();
                Platform.runLater(()->{
                    webcam.close() ;
                    primaryStage.close();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        /*---------------------------------------Recive THread------------------------------*/
        Thread reciveThread = new Thread(()->
        {
            BufferedImage bufferedImage;
            while (VedioCalling)
            {
                ImageIcon ic  ;
                Image picture ;
                try
                {
                    ic = (ImageIcon) ois.readObject();
                    bufferedImage = new BufferedImage(ic.getIconWidth(),ic.getIconHeight(),BufferedImage.TYPE_INT_RGB) ;
                    Graphics g = bufferedImage.createGraphics() ;
                    ic.paintIcon(null,g,0,0);
                    g.dispose();
                    picture = SwingFXUtils.toFXImage(bufferedImage,null);
                    user2.setImage(picture);
                }
                catch (IOException | ClassNotFoundException e)
                {
                    VedioCalling =false ;
                }
            }
            try
            {
                socket.close();
                Platform.runLater(()->{
                    webcam.close() ;
                    primaryStage.close();
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        reciveThread.start();
        sendVideo.start();
        /*----------------------------------------------------------------------------*/
    }


    public  class VedioCall  implements Runnable
    {
        Socket s;
        ObjectInputStream ois ;
        ObjectOutputStream objectOutputStream ;
        ImageView myimage ;
        ImageView user2 ;
        JFXButton btnEnd , audioCallBtn ;

        Stage primaryStage ;
        //  final Dimension size  = WebcamResolution.QVGA.getSize();
        Webcam webcam = Webcam.getDefault();


        public VedioCall(Socket s,ObjectInputStream ois ) throws IOException
        {
            this.s = s;
            this.ois = ois;
            this.objectOutputStream = new ObjectOutputStream(s.getOutputStream());
            this.webcam = Webcam.getDefault();
            this.webcam.setViewSize(WebcamResolution.QVGA.getSize());
            this.webcam.open();

        }

        @Override
        public void run()
        {
            VedioCalling =true ;
            VedoInterface();


        }
        /*---------------------------------------------------------------------------*/
        public void VedoInterface()
        {


            Platform.runLater(()->
            {
                VedioCalling =true ;

                primaryStage = new Stage();
                Pane pane = new Pane();
                pane.setPrefHeight(400.0);
                pane.setPrefWidth(858.0);
                pane.setStyle("-fx-background-color: #F0F0F0; -fx-background-radius: 15px;");

                myimage = new ImageView();
                myimage.setX(50);
                myimage.setY(60);
                myimage.setFitWidth(350);
                myimage.setFitHeight(200);
                myimage.setStyle("-fx-border-color: #4CAF50; -fx-border-width: 5px; -fx-background-radius: 15px;");

                user2 = new ImageView();
                user2.setX(450);
                user2.setY(60);
                user2.setFitWidth(350);
                user2.setFitHeight(200);
                user2.setStyle("-fx-border-color: #FF9800; -fx-border-width: 5px; -fx-background-radius: 15px;");

                Label timeLabel = new Label();
                timeLabel.setText("Thời gian: " + java.time.LocalTime.now().withSecond(0).withNano(0)); // Lấy thời gian hiện tại
                timeLabel.setStyle("-fx-text-fill: black;");
                timeLabel.setLayoutX(50);
                timeLabel.setLayoutY(300);

                Text endCall = GlyphsDude.createIcon(FontAwesomeIconName.PHONE, "2em");
                endCall.setFill(Color.WHITE);
                btnEnd = new JFXButton();
                btnEnd.setStyle("-fx-background-color: #E9505B; -fx-background-radius: 20px");
                btnEnd.setPrefSize(50, 50);
                btnEnd.setGraphic(endCall);
                btnEnd.setLayoutX(350);
                btnEnd.setLayoutY(300);

                Text micSlash = GlyphsDude.createIcon(FontAwesomeIconName.MICROPHONE_SLASH, "2em");
                micSlash.setFill(Color.WHITE);
                audioCallBtn = new JFXButton();
                audioCallBtn.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 20px; -fx-text-fill: white;");
                audioCallBtn.setPrefSize(50, 50);
                audioCallBtn.setGraphic(micSlash);
                audioCallBtn.setLayoutX(450);  // Canh giữa nút (đặt vị trí theo chiều ngang)
                audioCallBtn.setLayoutY(300);  // Đặt vị trí gần dưới cùng


                try {
                    AudioCall(new ActionEvent() , audioCallBtn );
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }


// Thêm các phần tử vào pane
                pane.getChildren().addAll(myimage, user2, btnEnd, audioCallBtn, timeLabel);

// Đưa pane vào scene và hiển thị
                Scene scene = new Scene(pane);
                primaryStage.setScene(scene);
                primaryStage.setTitle("Cuộc Họp Video");
                primaryStage.show();

                btnEnd.setOnMouseClicked(e ->
                {
                    VedioCalling=false ;
                    primaryStage.close();
                    webcam.close() ;
                });

            });

            Thread sendVideo = new Thread(()->
            {
                BufferedImage bufferedImage;
                while (VedioCalling)
                {
                    ImageIcon ic;
                    bufferedImage = webcam.getImage();
                    Image picture = SwingFXUtils.toFXImage(bufferedImage,null);
                    myimage.setImage(picture);
                    ic = new ImageIcon(bufferedImage);
                    try
                    {
                        objectOutputStream.writeObject(ic);
                    }
                    catch (IOException e)
                    {
                        VedioCalling = false ;
                    }

                }
                try {
                    s.close();
                    Platform.runLater(()->{
                        primaryStage.close();
                        webcam.close() ;
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Thread reciveVideo = new Thread(()->
            {
                BufferedImage bufferedImage;
                while (VedioCalling)
                {
                    ImageIcon ic  ;
                    Image picture ;
                    try
                    {
                        ic = (ImageIcon) ois.readObject();
                        if (ic == null || ic.getImageLoadStatus() != MediaTracker.COMPLETE) {
                            System.err.println("Received ImageIcon is null or incomplete");
                            continue; // Bỏ qua khung hình hiện tại
                        }
                        bufferedImage = new BufferedImage(ic.getIconWidth(),ic.getIconHeight(),BufferedImage.TYPE_INT_RGB) ;
                        Graphics g = bufferedImage.createGraphics() ;
                        ic.paintIcon(null,g,0,0);
                        g.dispose();
                        picture = SwingFXUtils.toFXImage(bufferedImage,null);
                        user2.setImage(picture);
                    }
                    catch (IOException | ClassNotFoundException e)
                    {
                        VedioCalling =false ;
                    }
                }
                try
                {
                    s.close();
                    Platform.runLater(()->{
                        primaryStage.close(); ;
                        webcam.close() ;
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            sendVideo.start();
            reciveVideo.start();
        }
    }
    /*******************************************************************************************************************
     *                                                                                                                 *
     *                                                   Audio Call                                                    *
     *                                                                                                                 *
     ******************************************************************************************************************/
//    @FXML
//    private JFXButton AudioCallBtn;

    /*--------------------------------------------------------------------------------*/

    @FXML
    void AudioCall(ActionEvent event ,JFXButton audioCallBtn) throws IOException
    {
        String ip = chatSocket.getInetAddress().getHostAddress();
        int port = portUser2 ;
        System.out.println("ip "+ip+ " port "+port);

        Socket socketAudio = new Socket(ip,port);

        ObjectOutputStream oos = new ObjectOutputStream(socketAudio.getOutputStream());
        oos.writeObject(4);
        /*--------------------------------*/
        audioCallBtn.setOnMouseClicked(e -> {
            if (Radio) {
                // Tắt mic
                Radio = false;
                Text micSlash = GlyphsDude.createIcon(FontAwesomeIconName.MICROPHONE_SLASH, "2em");
                micSlash.setFill(Color.WHITE);
                audioCallBtn.setGraphic(micSlash);

                try {
                    if (socketAudio != null && !socketAudio.isClosed()) {
                        socketAudio.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } else {
                // Bật mic
                Radio = true;
                Text mic = GlyphsDude.createIcon(FontAwesomeIconName.MICROPHONE, "2em");
                mic.setFill(Color.WHITE);
                audioCallBtn.setGraphic(mic);

                iniAudioClient(ip, port);
                //   iniAudioServer(MyPort);

            }
        });

        /*--------------------------------*/

    }
    public void iniAudioClient(String ip ,int port)
    {
        new Thread(() -> {
            AudioFormat format = getaudioFormat();
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
            if (!AudioSystem.isLineSupported(info)) {
                System.out.println("not supported");
                System.exit(0);
            }
            try {
                audio_in = (TargetDataLine) AudioSystem.getLine(info);
                audio_in.open(format);
                audio_in.start();
                record_Thread r = new record_Thread();
                InetAddress inet = InetAddress.getByName(ip);
                r.audio_in = audio_in;
                r.dout = new DatagramSocket();
                r.server_ip = inet;
                r.server_port = port;
                Radio = true ;
                r.start();
            } catch (LineUnavailableException | UnknownHostException | SocketException e) {
                e.printStackTrace();
            }
        }).start();
    }
    public void iniAudioServer(int port)
    {
        new Thread(() -> {
            SourceDataLine audio_in;
            AudioFormat format = getaudioFormat();
            DataLine.Info info_out = new DataLine.Info(SourceDataLine.class, format);
            if (!AudioSystem.isLineSupported(info_out)) {
                System.out.println("not supported");
                System.exit(0);
            }
            try {
                audio_in = (SourceDataLine) AudioSystem.getLine(info_out);
                audio_in.open(format);
                audio_in.start();
                player_thread p = new player_thread();
                p.din = new DatagramSocket(port);
                p.audio_out = audio_in;
                Radio = true ;
                p.start();
            } catch (LineUnavailableException | SocketException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public  class AudioCall  implements Runnable
    {
        Socket s;

        public AudioCall(Socket s)
        {
            this.s = s;
        }

        @Override
        public void run()
        {
            createFrame() ;
        }
        public void createFrame()
        {
            Platform.runLater(()->
            {
                String ip = chatSocket.getInetAddress().getHostAddress() ;
                int port = portUser2 ;

                iniAudioServer(MyPort);
                //   iniAudioClient(ip,port);

            });
        }
    }


    public static AudioFormat getaudioFormat()
    {
        float simpleRate = 8000.0F ;
        int simpleSireInbits = 8 ;
        int channel = 1 ;
        boolean signed = true ;
        boolean bigIndian = false ;
        return new AudioFormat(simpleRate,simpleSireInbits,channel,signed,bigIndian) ;
    }
    TargetDataLine  audio_in ;
    public static boolean Radio = false ;
    public static boolean VedioCalling = false ;

}
