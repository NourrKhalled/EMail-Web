package com.team.email;
import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.fasterxml.jackson.databind.ObjectMapper;

public class User implements UserInterface {
 
    private String userName;
    private String password;
    private Contacts contacts;
    private MailFolders mailFolders;



    public User() {
    }
   
    private User (UserBuilder builder){
    
        this.userName = builder.userName;
        this.password = builder.password;
        this.contacts = builder.contacts;
        this.mailFolders = builder.mailFolders;

    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public Contacts getContacts() {
        return this.contacts;
    }

    public void setContacts(Contacts contacts) {
        this.contacts = contacts;
    }

    public MailFolders getMailFolders() {
        return this.mailFolders;
    }

    public void setMailFolders(MailFolders mailFolders) {
        this.mailFolders = mailFolders;
    }

     public void save() throws Exception {
        String currentDir = System.getProperty("user.dir");
        currentDir =Paths.get(currentDir, "dataBase").toString(); 
        Path folderPath = Paths.get(currentDir, this.userName);
        Path filePath = Paths.get(folderPath.toString(), "User.json");
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File(filePath.toString());
        objectMapper.writeValue(jsonFile, this);
        validateSchema(jsonFile);

        System.out.println("User saved");
    }

    public User load(String userName) throws Exception {
        String currentDir = System.getProperty("user.dir");
        currentDir =Paths.get(currentDir, "dataBase").toString(); 
        Path folderPath = Paths.get(currentDir, userName);
        Path filePath = Paths.get(folderPath.toString(), "User.json");
        ObjectMapper objectMapper = new ObjectMapper();
        File jsonFile = new File(filePath.toString());
        //validateSchema(jsonFile); 
        return objectMapper.readValue(jsonFile, User.class);
    }
    

    public void recievMail(Mail mail){
        System.out.println(userName);
        this.mailFolders.getInboxFolder().add(mail);
        for(Contact contact:this.getContacts().getContacts()){
            for(String email:contact.getEmails()){
                if(mail.getSender().equals(email)){
                    contact.getMails().add(mail);
                    break;
                }
            }
        }
        try{
        this.save();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    
    @Override
    public void sendMail(Vector<Attachment> attachments,Vector<String> recipients,String subject ,int priority ,String body,String date){
        this.mailFolders.sendMail(attachments, this.userName, recipients, subject, priority, body, date);
        try{
            this.save();
            }
            catch(Exception e){
                System.out.println(e);
            }
    }
    
    @Override
    public void makeDraft(Vector<Attachment> attachments,String sender,Vector<String> recipients,String subject ,int priority ,String body,String date){
        this.mailFolders.makeDraft(attachments, sender, recipients, subject, priority, body, date);
        try{
            this.save();
            }
            catch(Exception e){
                System.out.println(e);
            }
    }
    
    @Override
    public void MoveToTrash(String folderName,String date,String DeleteDate){
        this.mailFolders.MoveToTrash(folderName, date, DeleteDate);
        try{
            this.save();
            }
            catch(Exception e){
                System.out.println(e);
            }
        
    }

    @Override
    public void returnFromTrash(String date){
        this.mailFolders.returnFromTrash(date);
        try{
            this.save();
            }
            catch(Exception e){
                System.out.println(e);
            }
    }
    
    @Override
    public void deleteMail(String folderName,String date){
        this.mailFolders.deleteMail(folderName, date);
        try{
            this.save();
            }
            catch(Exception e){
                System.out.println(e);
            }
    }
    
    @Override
    public void moveToFolder(String oldFolder,String newFolder,String date){
        this.mailFolders.moveToFolder(oldFolder, newFolder, date);
        try{
            this.save();
            }
            catch(Exception e){
                System.out.println(e);
            }
    }
    
    @Override
    public void addUserFolder(String folderName){
        this.mailFolders.addUserFolder(folderName);
        try{
            this.save();
            }
            catch(Exception e){
                System.out.println(e);
            }
    }
    
    @Override
    public void renameUserFolder(String oldFolder,String newFolder){
        this.mailFolders.renameUserFolder(oldFolder, newFolder);
        try{
            this.save();
            }
            catch(Exception e){
                System.out.println(e);
            }
    }
    
    @Override
    public void deleteUserFolder(String folderName){
        this.mailFolders.deleteUserFolder(folderName);
        try{
            this.save();
            }
            catch(Exception e){
                System.out.println(e);
            }
    }

    @Override
    public void editContact(String ID, String contactName ,Vector<String> mails){
        this.getContacts().EditContact(ID, contactName, mails);
        try {
            this.save();
        } catch (Exception ex) {
        }
    }

    @Override
    public void deletecontact(String Id){
        this.getContacts().DeleteContact(Id);
        try {
            this.save();
        } catch (Exception ex) {
        }
    }

    @Override
    public void addContact(String ID,String contactName,Vector<String> UserNames){
        this.getContacts().AddContact(ID, contactName, UserNames);
        try {
            this.save();
        } catch (Exception ex) {
        }
    }


    private static void validateSchema(File jsonFile) throws Exception {

        FileInputStream schemaStream = new FileInputStream(SchemaConfig.SCHEMA_PATH);
        JSONObject schemaJSON = new JSONObject(new JSONTokener(schemaStream));
        Schema schema = SchemaLoader.load(schemaJSON);

        FileInputStream jsonStream = new FileInputStream(jsonFile);
        JSONObject jsonObject = new JSONObject(new JSONTokener(jsonStream));

     
        schema.validate(jsonObject); 
    }
    public static class UserBuilder{

    private String userName;
    private String password;
    private Contacts contacts;
    private MailFolders mailFolders;

    public UserBuilder(String userName, String password){
       this.userName = userName;
       this.password = password;
    }
    public UserBuilder setContacts(Contacts contacts){
        this.contacts = contacts;
        return this;
    }

    public UserBuilder setMailFolders(MailFolders mailFolders){
        this.mailFolders= mailFolders;
        return this;
    }
    public User build(){
        User user = new User(this);
        return user;
    }

    }

}