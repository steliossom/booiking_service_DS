import javax.swing.*;
import java.awt.*;
import java.rmi.Naming;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserClientGUI extends JFrame {
    private BookingService bookingService;
    private User currentUser;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> performanceBox = new JComboBox<>();
    private JTextField ticketCountField;
    private JTextField searchField;
    private String selectedTitle;

private HashMap<Performance, Double> performancePrices = new HashMap<>();
private List<TicketOrder> orders = new ArrayList<>();


    
    private JPanel mainPanel;
        List<String> codes = Arrays.asList("1234", "3657", "8910", "1112");
            

    public UserClientGUI() {
        setTitle("Ticket Booking");
        setSize(600, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        buildLoginUI();

        try {
            bookingService = (BookingService) Naming.lookup("rmi://localhost/BookingService");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Σφάλμα σύνδεσης με τον server: " + e.getMessage());
        }
    }

    private void buildLoginUI() {
        mainPanel = new JPanel(new GridLayout(4, 2));
        usernameField = new JTextField();
        passwordField = new JPasswordField();

        mainPanel.add(new JLabel("Όνομα χρήστη:"));
        mainPanel.add(usernameField);
        mainPanel.add(new JLabel("Κωδικός:"));
        mainPanel.add(passwordField);

        JButton loginBtn = new JButton("Σύνδεση");
        JButton registerBtn = new JButton("Εγγραφή");

        loginBtn.addActionListener(e -> login());
        registerBtn.addActionListener(e -> showRegistrationForm());

        mainPanel.add(loginBtn);
        mainPanel.add(registerBtn);

        add(mainPanel, BorderLayout.CENTER);
    }

   private void showRegistrationForm() {
    JTextField nameField = new JTextField();
    JTextField phoneField = new JTextField();
    JTextField emailField = new JTextField();
    JTextField usernameField = new JTextField();
    JPasswordField passwordField = new JPasswordField();

    JComboBox<String> roleComboBox = new JComboBox<>(new String[]{"user", "admin"});

    JTextField adminCodeField = new JTextField(); 
    adminCodeField.setEnabled(false); 

    roleComboBox.addActionListener(e -> {
        if (roleComboBox.getSelectedItem().equals("admin")) {
            adminCodeField.setEnabled(true); 
        } else {
            adminCodeField.setEnabled(false);
        }
    });

    JPanel panel = new JPanel(new GridLayout(8, 2)); 
    panel.add(new JLabel("Ονοματεπώνυμο:"));
    panel.add(nameField);
    panel.add(new JLabel("Τηλέφωνο:"));
    panel.add(phoneField);
    panel.add(new JLabel("Email:"));
    panel.add(emailField);
    panel.add(new JLabel("Username:"));
    panel.add(usernameField);
    panel.add(new JLabel("Password:"));
    panel.add(passwordField);
    panel.add(new JLabel("Ρόλος:"));
    panel.add(roleComboBox);
    panel.add(new JLabel("Αριθμός Επαλήθευσης (μόνο για admin):"));
    panel.add(adminCodeField); 

    int result = JOptionPane.showConfirmDialog(this, panel, "Εγγραφή Χρήστη", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
        String selectedRole = (String) roleComboBox.getSelectedItem(); 
        String enteredCode = adminCodeField.getText().trim();  

        if (selectedRole.equals("admin") && (enteredCode.isEmpty() || !codes.contains(enteredCode))) {
            JOptionPane.showMessageDialog(this, "Λάθος ή μη έγκυρος αριθμός επαλήθευσης.");
            return;
        }

        User newUser = new User(
                nameField.getText(),
                phoneField.getText(),
                emailField.getText(),
                usernameField.getText(),
                new String(passwordField.getPassword()),
                selectedRole
        );

        try {
            boolean success = bookingService.registerUser(newUser);
            if (success) {
                JOptionPane.showMessageDialog(this, "Επιτυχής εγγραφή. Συνδεθείτε τώρα.");
            } else {
                JOptionPane.showMessageDialog(this, "Η εγγραφή απέτυχε. Ίσως υπάρχει ήδη το username.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Σφάλμα εγγραφής: " + ex.getMessage());
        }
    }
}

   
   private void showDeleteUserForm() {
    JTextField usernameField = new JTextField();
    JPasswordField passwordField = new JPasswordField();

    JPanel panel = new JPanel(new GridLayout(2, 2));
    panel.add(new JLabel("Username:"));
    panel.add(usernameField);
    panel.add(new JLabel("Password:"));
    panel.add(passwordField);

    int result = JOptionPane.showConfirmDialog(this, panel, "Διαγραφή Χρήστη", JOptionPane.OK_CANCEL_OPTION);
    if (result == JOptionPane.OK_OPTION) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        try {
            boolean success = bookingService.deleteUser(username, password);
            if (success) {
                JOptionPane.showMessageDialog(this, "Ο χρήστης διαγράφηκε με επιτυχία.");
            } else {
                JOptionPane.showMessageDialog(this, "Αποτυχία διαγραφής. Ελέγξτε τα στοιχεία.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Σφάλμα διαγραφής: " + ex.getMessage());
        }
    }
}



    private void login() {
    String username = usernameField.getText();
    String password = new String(passwordField.getPassword());

    try {
        currentUser = bookingService.login(username, password);
        if (currentUser != null) {
            JOptionPane.showMessageDialog(this, "Καλώς ήρθες, " + currentUser.getName());

            if (currentUser.getRole().equalsIgnoreCase("admin")) {
                getContentPane().removeAll();
                buildAdminUI();  
            } else if (currentUser.getRole().equalsIgnoreCase("user")) {
                getContentPane().removeAll();
                buildClientUI(); 
            }
            revalidate();
            repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Λάθος στοιχεία σύνδεσης.");
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Σφάλμα σύνδεσης: " + e.getMessage());
    }
}

   private void buildAdminUI() {
       try{
    setLayout(new BorderLayout());
        List<Show> shows = bookingService.getAvailableShows();
shows.forEach(show -> System.out.println(show.getTitle()));   
    JPanel adminPanel = new JPanel(new GridLayout(5, 2, 5, 5)); 

    JButton addShowBtn = new JButton("Προσθήκη Show");
    JButton removeShowBtn = new JButton("Απενεργοποίηση Show");
    JButton viewShowsBtn = new JButton("Προβολή Show");
    JButton deleteUserBtn = new JButton("Διαγραφή Χρήστη"); 
JButton addPerformanceToExistingShowBtn = new JButton("Προσθήκη Παράστασης σε Υπάρχον Show");
addPerformanceToExistingShowBtn.addActionListener(e -> addPerformanceToExistingShow());

adminPanel.add(addPerformanceToExistingShowBtn); 

    addShowBtn.addActionListener(e -> addShow());
    removeShowBtn.addActionListener(e -> deactivateShow());
    viewShowsBtn.addActionListener(e -> viewShows());
    deleteUserBtn.addActionListener(e -> showDeleteUserForm()); 

    adminPanel.add(addShowBtn);
    adminPanel.add(removeShowBtn);
    adminPanel.add(viewShowsBtn);
    adminPanel.add(deleteUserBtn);

    JButton logoutBtn = new JButton("Αποσύνδεση");
    logoutBtn.addActionListener(e -> logout());

    adminPanel.add(new JLabel()); // Κενό κελί
    adminPanel.add(logoutBtn);

    adminPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    add(adminPanel, BorderLayout.CENTER);
       }
       catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Σφάλμα σύνδεσης με τον server.", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
    }
}

private void addShow() {
    JDialog dialog = new JDialog(this, "Προσθήκη Show", true);
    dialog.setSize(500, 400);
    dialog.setLayout(new BorderLayout());

    JPanel inputPanel = new JPanel(new GridLayout(2, 2));
    JLabel titleLabel = new JLabel("Τίτλος:");
    JTextField titleField = new JTextField();

    JLabel typeLabel = new JLabel("Τύπος:");
    JTextField typeField = new JTextField();

    inputPanel.add(titleLabel);
    inputPanel.add(titleField);
    inputPanel.add(typeLabel);
    inputPanel.add(typeField);

    DefaultListModel<String> performanceListModel = new DefaultListModel<>();
    List<Performance> performanceList = new ArrayList<>();

    JList<String> performanceListUI = new JList<>(performanceListModel);
    JScrollPane listScrollPane = new JScrollPane(performanceListUI);

    JButton addPerformanceBtn = new JButton("Προσθήκη Παράστασης");
    addPerformanceBtn.addActionListener(e -> {
        JDialog perfDialog = new JDialog(dialog, "Νέα Παράσταση", true);
        perfDialog.setSize(350, 300);
        perfDialog.setLayout(new GridLayout(5, 2));

        JTextField dateField = new JTextField();
        JTextField timeField = new JTextField();
        JTextField seatsField = new JTextField();
        JTextField priceField = new JTextField();

        perfDialog.add(new JLabel("Ημερομηνία (dd/MM/yyyy):"));
        perfDialog.add(dateField);
        perfDialog.add(new JLabel("Ώρα (HH:mm):"));
        perfDialog.add(timeField);
        perfDialog.add(new JLabel("Διαθέσιμες Θέσεις:"));
        perfDialog.add(seatsField);
        perfDialog.add(new JLabel("Τιμή:"));
        perfDialog.add(priceField);

        JButton saveBtn = new JButton("Αποθήκευση");
        saveBtn.addActionListener(ev -> {
            try {
                String dateText = dateField.getText().trim();
                String timeText = timeField.getText().trim();

                String dateTimeText = dateText + " " + timeText;
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                Date date = sdf.parse(dateTimeText);

                int seats = Integer.parseInt(seatsField.getText().trim());
                String priceText = priceField.getText().trim().replace("€", "").replace(",", ".");
                double price = Double.parseDouble(priceText);

                Performance perf = new Performance(date, seats, price, true);
                performanceList.add(perf);

                String formattedDate = sdf.format(date);
                performanceListModel.addElement(String.format(
                    "Ημερομηνία: %s | Θέσεις: %d | Τιμή: %.2f€",
                    formattedDate, seats, price
                ));

                perfDialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(perfDialog, "Λάθος δεδομένα", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            }
        });

        perfDialog.add(new JLabel()); 
        perfDialog.add(saveBtn);

        perfDialog.setVisible(true);
    });

    JButton saveShowBtn = new JButton("Αποθήκευση Show");
    saveShowBtn.addActionListener(e -> {
        String title = titleField.getText().trim();
        String type = typeField.getText().trim();

        if (title.isEmpty() || type.isEmpty() || performanceList.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Συμπληρώστε όλα τα πεδία και προσθέστε τουλάχιστον μία παράσταση.", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            Show newShow = new Show(title, type, performanceList);
            bookingService.addShow(newShow);  
            JOptionPane.showMessageDialog(dialog, "Το show αποθηκεύτηκε επιτυχώς.");
            dialog.dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Σφάλμα κατά την αποθήκευση.", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
        }
    });

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(addPerformanceBtn);
    buttonPanel.add(saveShowBtn);

    dialog.add(inputPanel, BorderLayout.NORTH);
    dialog.add(listScrollPane, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);

    dialog.setVisible(true);
}


private void deactivateShow() {
    try {
        List<Show> shows = bookingService.getAvailableShows();

        if (shows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Δεν υπάρχουν show για απενεργοποίηση.");
            return;
        }

        String[] showTitles = shows.stream()
                .map(Show::getTitle)
                .distinct()
                .toArray(String[]::new);

        JComboBox<String> showComboBox = new JComboBox<>(showTitles);
        int result = JOptionPane.showConfirmDialog(
                this,
                showComboBox,
                "Επιλογή Show για Απενεργοποίηση",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            String selectedTitle = (String) showComboBox.getSelectedItem();

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Είσαι σίγουρος/η ότι θέλεις να αφαιρέσεις το show " + selectedTitle + ";",
                    "Επιβεβαίωση",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                boolean removed = bookingService.deactivateShow(selectedTitle); 
                System.out.println(selectedTitle);
                System.out.println(removed);

                if (removed) {
                    JOptionPane.showMessageDialog(this, "Το show αφαιρέθηκε επιτυχώς.");
                } else {
                    JOptionPane.showMessageDialog(this, "Αποτυχία απενεργοποίησης show.");
                }
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Σφάλμα κατά την απενεργοποίηση show: " + e.getMessage());
        e.printStackTrace();
    }
}

private void viewShows() {
    try {
        List<Show> shows = bookingService.getAvailableShows();

        if (shows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Δεν υπάρχουν διαθέσιμα show.");
            return;
            
        }
        JDialog dialog = new JDialog(this, "Προβολή show", true);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        StringBuilder sb = new StringBuilder();
        for (Show show : shows) {
            sb.append("Τίτλος: ").append(show.getTitle()).append("\n");
            sb.append("Τύπος: ").append(show.getType()).append("\n");

            for (Performance perf : show.getPerformances()) {
                Date dt = perf.getDateTime();
                sb.append("Παράσταση: ")
                  .append(String.format("%1$td/%1$tm/%1$tY %1$tH:%1$tM", dt))
                  .append(" | Διαθέσιμες Θέσεις: ").append(perf.getAvailableSeats())
                  .append(" | Τιμή: ").append(perf.getPrice()).append("€")
                  .append("\n");
            }
            sb.append("\n");
        }

        textArea.setText(sb.toString());

        JScrollPane scrollPane = new JScrollPane(textArea);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JButton closeButton = new JButton("Κλείσιμο");
        closeButton.addActionListener(e -> dialog.dispose());
        dialog.add(closeButton, BorderLayout.SOUTH);

        dialog.setVisible(true);

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Σφάλμα σύνδεσης με τον server.", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
    }
}

private void addPerformanceToExistingShow() {
    try {
        List<Show> shows = bookingService.getAvailableShows();

        if (shows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Δεν υπάρχουν παραστάσεις για προσθήκη.");
            return;
        }

        String[] showTitles = shows.stream()
                .map(Show::getTitle)
                .distinct()
                .toArray(String[]::new);

        JComboBox<String> showComboBox = new JComboBox<>(showTitles);
        int result = JOptionPane.showConfirmDialog(
                this,
                showComboBox,
                "Επιλογή Παράστασης για Προσθήκη",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {
            String selectedTitle = (String) showComboBox.getSelectedItem();

            Show selectedShow = null;
            for (Show show : shows) {
                if (show.getTitle().equals(selectedTitle)) {
                    selectedShow = show;
                    break;
                }
            }

            if (selectedShow != null) {
                addPerformanceToShowDialog(selectedShow);
            }
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Σφάλμα κατά την προσθήκη παράστασης: " + e.getMessage());
        e.printStackTrace();
    }
}

private void addPerformanceToShowDialog(Show selectedShow) {
    JDialog dialog = new JDialog(this, "Προσθήκη Παράστασης", true);
    dialog.setSize(500, 400);
    dialog.setLayout(new BorderLayout());

    JPanel inputPanel = new JPanel(new GridLayout(4, 2));
    JLabel dateLabel = new JLabel("Ημερομηνία (dd/MM/yyyy):");
    JTextField dateField = new JTextField();

    JLabel timeLabel = new JLabel("Ώρα (HH:mm):");
    JTextField timeField = new JTextField();

    JLabel seatsLabel = new JLabel("Διαθέσιμες Θέσεις:");
    JTextField seatsField = new JTextField();

    JLabel priceLabel = new JLabel("Τιμή:");
    JTextField priceField = new JTextField();

    inputPanel.add(dateLabel);
    inputPanel.add(dateField);
    inputPanel.add(timeLabel);
    inputPanel.add(timeField);
    inputPanel.add(seatsLabel);
    inputPanel.add(seatsField);
    inputPanel.add(priceLabel);
    inputPanel.add(priceField);

    JButton saveBtn = new JButton("Αποθήκευση Παράστασης");
    saveBtn.addActionListener(e -> {
        try {
            String dateText = dateField.getText().trim();
            String timeText = timeField.getText().trim();

            String dateTimeText = dateText + " " + timeText;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Date date = sdf.parse(dateTimeText);

            int seats = Integer.parseInt(seatsField.getText().trim());
            String priceText = priceField.getText().trim().replace("€", "").replace(",", ".");
            double price = Double.parseDouble(priceText);

            Performance newPerformance = new Performance(date, seats, price, true);
            selectedShow.addPerformance(newPerformance); 

            bookingService.updateShow(selectedShow); 
            JOptionPane.showMessageDialog(dialog, "Η παράσταση προστέθηκε επιτυχώς.");
            dialog.dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(dialog, "Σφάλμα κατά την προσθήκη παράστασης.", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
        }
    });

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(saveBtn);

    dialog.add(inputPanel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);

    dialog.setVisible(true);
}


private void buildClientUI() {
    try {
        setLayout(new BorderLayout());

        List<Show> showsList = bookingService.getAvailableShows();
for (Show show : showsList) {
    for (Performance perf : show.getPerformances()) {
        performancePrices.put(perf, perf.getPrice());
    }
}
        String[] dropdownOptions = showsList.stream()
                .map(Show::getTitle)
                .distinct()
                .toArray(String[]::new);

        JComboBox<String> dropdownList = new JComboBox<>(dropdownOptions);
        dropdownList.addActionListener(e -> {
            selectedTitle = (String) dropdownList.getSelectedItem();
            performanceBox.removeAllItems();


           
            
            for (Show show : showsList) {
                if (show.getTitle().equals(selectedTitle)) {
                    for (Performance p : show.getPerformances()) {
                         Date dt = p.getDateTime();
                        performanceBox.addItem(String.format("%1$td/%1$tm/%1$tY %1$tH:%1$tM", dt));
                    }
                    break;
                }
            }
        });

        if (!showsList.isEmpty()) {
            dropdownList.setSelectedIndex(0); 
        }

        JPanel panel = new JPanel();
        add(panel, BorderLayout.NORTH);

        JPanel clientPanel = new JPanel(new GridLayout(6, 2, 5, 5));

        ticketCountField = new JTextField(5);

        JButton deleteUserBtn = new JButton("Διαγραφή Χρήστη"); 

        deleteUserBtn.addActionListener(e -> showDeleteUserForm()); 

        JButton cancelOrderBtn = new JButton("Ακύρωση Παρσγγελίας");
        
        cancelOrderBtn.addActionListener(e-> cancelOrder());
        
        JButton refreshBtn = new JButton("Αναζήτηση Show");
        refreshBtn.addActionListener(e ->{
        searchField = new JTextField(20);
        
 JDialog dialog = new JDialog(this, "Αναζήτηση Show", true);
    dialog.setSize(500, 400);
    dialog.setLayout(new BorderLayout());
JDialog perfDialog = new JDialog(dialog, "Αναζήτηση Show", true);
        perfDialog.setSize(350, 300);
        perfDialog.setLayout(new GridLayout(4, 3));

      
        perfDialog.add(new JLabel("Αναζήτηση"));
        perfDialog.add(searchField);
       

        JButton searchBtn = new JButton("Αναζήτηση");
                perfDialog.add(searchBtn);
searchBtn.addActionListener(ev -> {
     String query = searchField.getText().trim().toLowerCase(); 
    for (int i = 0; i < dropdownList.getItemCount(); i++) {
        String item = (String) dropdownList.getItemAt(i);
        if (item.toLowerCase().contains(query)) { 
            dropdownList.setSelectedItem(item);
            break;
        }
    }

});

            perfDialog.setVisible(true);


        }
        );
        selectedTitle = (String) dropdownList.getSelectedItem();
        JButton orderBtn = new JButton("Παραγγελία Εισιτηρίων");
        orderBtn.addActionListener(e -> placeOrder(selectedTitle));

        clientPanel.add(new JLabel("Show:"));
        clientPanel.add(dropdownList);
        clientPanel.add(new JLabel("Παράσταση:"));
        clientPanel.add(performanceBox);
        clientPanel.add(new JLabel("Αριθμός εισιτηρίων:"));
        clientPanel.add(ticketCountField);
        clientPanel.add(refreshBtn);
        clientPanel.add(orderBtn);
        clientPanel.add(deleteUserBtn); 
        clientPanel.add(cancelOrderBtn);
        JButton logoutBtn = new JButton("Αποσύνδεση");
        logoutBtn.addActionListener(e -> logout());

        clientPanel.add(new JLabel()); 
        clientPanel.add(logoutBtn);

        clientPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(clientPanel, BorderLayout.CENTER);
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Σφάλμα σύνδεσης με τον server.", "Σφάλμα", JOptionPane.ERROR_MESSAGE);
    }
}


private void placeOrder(String title) {
String selectedPerformance = (String) performanceBox.getSelectedItem();  
Collection<Double> prices = performancePrices.values();
String fullName = null;
String cardNumber = null;
Double cost = 0.0;
Performance perf = new Performance();
for (Map.Entry<Performance, Double> entry : performancePrices.entrySet()) {
    perf = entry.getKey();
    Double price = entry.getValue();
      String selectedText = (String) performanceBox.getSelectedItem();
String perfText = String.format("%1$td/%1$tm/%1$tY %1$tH:%1$tM", perf.getDateTime());

if (perfText.equals(selectedText)) {
        cost = price;
        break; 
    }
}

    if (selectedPerformance == null) {
        JOptionPane.showMessageDialog(this, "Δεν έχει επιλεγεί παράσταση.");
        return;
    }

    String performanceKey = title + " | " + selectedPerformance;
    int count;
    
    try {
        count = Integer.parseInt(ticketCountField.getText());
        if (count <= 0) {
            JOptionPane.showMessageDialog(this, "Ο αριθμός εισιτηρίων πρέπει να είναι θετικός.");
            return;
        }
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Μη έγκυρος αριθμός εισιτηρίων.");
        return;
    }

    try {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
Date date = sdf.parse(selectedPerformance);
double totalCost = cost * count;

JOptionPane.showMessageDialog(this,
    "Σύνολο: " + totalCost + " €",
    "Πληρωμή",
    JOptionPane.INFORMATION_MESSAGE);



        JTextField fullNameField = new JTextField();
        JTextField cardNumberField = new JTextField();
        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Ονοματεπώνυμο:"));
        panel.add(fullNameField);
        panel.add(new JLabel("Αριθμός Πιστωτικής Κάρτας:"));
        panel.add(cardNumberField);
        
        

        int result = JOptionPane.showConfirmDialog(null, panel, "Στοιχεία Πληρωμής",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        
        
       boolean cancelOrder = false;

if (result == JOptionPane.OK_OPTION) {
    fullName = fullNameField.getText().trim();
    cardNumber = cardNumberField.getText().trim();

    if (fullName.isEmpty() || !cardNumber.matches("\\d{16}")) {
        JOptionPane.showMessageDialog(null,
                "Η πληρωμή ακυρώθηκε. Δεν δόθηκαν σωστά στοιχεία.",
                "Ακύρωση",
                JOptionPane.WARNING_MESSAGE);
        cancelOrder = true;
    }
} else {
    JOptionPane.showMessageDialog(null,
            "Η πληρωμή ακυρώθηκε.",
            "Ακύρωση",
            JOptionPane.WARNING_MESSAGE);
    cancelOrder = true;
}        
          
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Σφάλμα παραγγελίας: " + e.getMessage());
        e.printStackTrace();
    }
}



private void cancelOrder() {
    if (orders.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Δεν υπάρχουν κρατήσεις για ακύρωση.");
        return;
    }

    List<String> orderDescriptions = new ArrayList<>();
    for (TicketOrder order : orders) {
        orderDescriptions.add(order.getTitle() + " | " + order.getSelectedPerformance());
    }

    String selectedOrder = (String) JOptionPane.showInputDialog(
            this,
            "Επιλέξτε κράτηση για ακύρωση:",
            "Ακύρωση Κράτησης",
            JOptionPane.PLAIN_MESSAGE,
            null,
            orderDescriptions.toArray(),
            null);

    if (selectedOrder == null) {
        return; 
    }

    TicketOrder toCancel = null;
    for (TicketOrder order : orders) {
        String desc = order.getTitle() + " | " + order.getSelectedPerformance();
        if (desc.equals(selectedOrder)) {
            toCancel = order;
            break;
        }
    }

    if (toCancel == null) {
        JOptionPane.showMessageDialog(this, "Η κράτηση δεν βρέθηκε.");
        return;
    }

    try {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        Date perfDate = sdf.parse(toCancel.getSelectedPerformance());
        Calendar perfCal = Calendar.getInstance();
        perfCal.setTime(perfDate);

        Calendar today = Calendar.getInstance();
        boolean sameDay = perfCal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                          perfCal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR);

        if (sameDay) {
            JOptionPane.showMessageDialog(this, "Η ακύρωση δεν επιτρέπεται για παραστάσεις που προβάλλονται σήμερα.");
            return;
        }

        boolean success = bookingService.cancelOrder(currentUser.getUsername(), toCancel.getTitle(), toCancel.getSelectedPerformance(), toCancel.getTickets());
        if (success) {
            orders.remove(toCancel);
            JOptionPane.showMessageDialog(this,
                    "Η ακύρωση ολοκληρώθηκε.\nΟι θέσεις αποδεσμεύθηκαν.",
                    "Επιβεβαίωση Ακύρωσης",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Η ακύρωση απέτυχε. Ελέγξτε τα στοιχεία ή τη διαθεσιμότητα.",
                    "Αποτυχία Ακύρωσης",
                    JOptionPane.WARNING_MESSAGE);
        }

    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Σφάλμα κατά την ακύρωση: " + e.getMessage());
        e.printStackTrace();
    }
}


    private void logout() {
        currentUser = null;
        getContentPane().removeAll();
        buildLoginUI();
        revalidate();
        repaint();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserClientGUI().setVisible(true));
    }
}
