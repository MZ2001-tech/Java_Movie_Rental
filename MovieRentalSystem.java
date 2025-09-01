import java.util.*;           // Import collections for data storage and manipulation
import java.util.stream.*;    // Import Stream API for functional programming (lambdas)

// ===============================
// CUSTOM EXCEPTION
// ===============================

/**
 * Custom checked exception for rental errors.
 * Used to signal issues like renting an already rented movie.
 */
class RentalException extends Exception{
    public RentalException(String message){
        super(message); // Pass error message to Exception base class
    }
}

// ===============================
// INTERFACE
// ===============================

/**
 * Rentable interface defines rental operations.
 * Enforces contract for rent and return methods.
 */
interface Rentable{
    void rent() throws RentalException;        // Rent an item, may throw RentalException
    void returnItems() throws RentalException; // Return an item, may throw RentalException
}

// ===============================
// ABSTRACT CLASS: Base class for Movies
// ===============================

/**
 * Abstract Movie class represents a generic movie.
 * Implements Rentable, so must define rent() and returnItems().
 * Cannot be instantiated directly; only subclasses can be created.
 */
abstract class Movie implements Rentable{
    protected String title;      // Movie title
    protected boolean isRented;  // Rental status
    protected int daysRented;    // Number of days rented

    public Movie(String title){
        this.title = title;      // Set movie title
        this.isRented = false;   // Movie starts as not rented
        this.daysRented = 0;     // No rental days yet
    }

    // Overloaded methods for displaying info (polymorphism)
    public void displayInfo(){System.out.println("Movie: "+ title);}
    public void displayInfo(String prefix){System.out.println(prefix+ " "+ title);}

    // Abstract method forces subclasses to define their own rental fee logic
    public abstract double getRentalFee();

    /**
     * Rent the movie if not already rented.
     * Updates object state and throws exception if already rented.
     */
    public void rent() throws RentalException{
        if(!isRented){ // Only rent if not already rented
            isRented = true;     // Update rental status
            daysRented = 0;      // Reset rental days
            System.out.println(title + " has been rented");
        }else{
            throw new RentalException(title + " is already rented."); // Exception for invalid state
        }
    }

    /**
     * Return the movie if it was rented.
     * Updates object state and throws exception if not rented.
     */
    public void returnItems() throws RentalException{
        if(isRented){
            isRented = false;    // Update rental status
            System.out.println(title+ " has been return total fee is: RM "+ calculateFee());
            daysRented = 0;
        }else{
            throw new RentalException(title+" was not rented."); // Exception for invalid state
        }
    }

    /**
     * Calculates rental fee including late fee.
     * Late fee: RM 2 per extra day after 3 days.
     * Encapsulates business logic for fee calculation.
     */
    public double calculateFee(){
        double fee = getRentalFee(); // Get base fee from subclass
        if(daysRented > 3){          // Check for late return
            fee += (daysRented - 3) * 2; // Add late fee
        }
        return fee; // Return total fee
    }

    /**
     * Simulate passing of days for rental.
     * Used to test late fee logic.
     */
    public void addDays(int days){
        this.daysRented = days; // Increment rental days
    }
}

// ===============================
// CHILD CLASSES: Digital & Physical Movies
// ===============================

/**
 * DigitalMovie class represents a digital movie.
 * Inherits from Movie and provides specific rental fee.
 */
class DigitalMovie extends Movie{
    public DigitalMovie(String title){super(title);}
    @Override
    public double getRentalFee(){return 5.0;} // Digital movie fee
}

/**
 * PhysicalMovie class represents a physical movie.
 * Inherits from Movie and provides specific rental fee.
 */
class PhysicalMovie extends Movie{
    public PhysicalMovie(String title){super(title);}
    @Override
    public double getRentalFee(){return 10.0;} // Physical movie fee
}

// ===============================
// MAIN CLASS
// ===============================
public class MovieRentalSystem{

    /**
     * Calculates total fees for a list of movies.
     * Uses Stream API and lambda for concise, functional processing.
     * Data flows: movieList → stream → mapToDouble (lambda) → sum
     */
    public static double calculateTotalFees(List<Movie>movieList){
        // The movieList contains Movie objects, which can be DigitalMovie or PhysicalMovie.
        // When we process the list, each object "remembers" its actual type (set at creation).
        // The Stream API lets us process each movie in a functional style.
        // For each movie 'm', we call m.calculateFee():
        //   - calculateFee() calls getRentalFee() on the actual object.
        //   - If 'm' is a DigitalMovie, getRentalFee() returns 5.0.
        //   - If 'm' is a PhysicalMovie, getRentalFee() returns 10.0.
        //   - calculateFee() also adds any late fees if daysRented > 3.
        // This is possible because of polymorphism: Java automatically calls the correct method for each object type.
        // The lambda (m -> m.calculateFee()) ensures each movie's fee is calculated using its own logic.
        // Finally, sum() adds up all the fees and returns the total.
        return movieList.stream()
            .mapToDouble(m -> m.calculateFee()) // Calls the correct fee logic for each movie type
            .sum(); // Returns the total fee for all movies
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in); // Scanner for user input

        // ===============================
        // DATA STRUCTURES
        // ===============================

        // ArrayList stores all movies (Movie objects) in the system
        ArrayList<Movie> movieList = new ArrayList<>(Arrays.asList(
            new DigitalMovie("Inception"),
            new PhysicalMovie("Dark Knight"),
            new DigitalMovie("Interstellar"),
            new PhysicalMovie("Tenet"),
            new DigitalMovie("Oppenheimer"),
            new PhysicalMovie("Spideman"),
            new DigitalMovie("Avengers Infinity war"),
            new PhysicalMovie("Harry Potter and The Half blood Prince"),
            new DigitalMovie("Spiderman No Way Home"),
            new PhysicalMovie("Superman 1978"),
            new DigitalMovie("Transformers one"),
            new PhysicalMovie("The Social Network")
        ));
        // Data flow: new Movie objects are created and stored in movieList

        // HashSet stores unique customer names (no duplicates)
        HashSet<String> customers = new HashSet<>(Arrays.asList("Alice", "Bob", "Charlie","Jack","Kim"));
        // Data flow: customer names are added to HashSet for fast lookup and uniqueness

        // LinkedList stores rental history (ordered, fast insert/remove)
        LinkedList<String> rentalHistory = new LinkedList<>();
        // Data flow: rental/return events are appended to rentalHistory

        // HashMap maps customer names to their list of rented movies
        HashMap<String, List<Movie>> rentals = new HashMap<>();
        // Data flow: customer name → List of Movie objects they have rented

        boolean running  = true; // Controls main loop (while user doesn't exit)
        while(running){
            // ===============================
            // MENU DISPLAY
            // ===============================
            System.out.println("\n--- Movie Rental System ---");
            System.out.println("1. Add a new customer");   // Add customer to system
            System.out.println("2. Add a new movie");      // Add movie to library
            System.out.println("3. Rent a movie");         // Rent a movie
            System.out.println("4. Return a movie");       // Return a rented movie
            System.out.println("5. Show all movies");      // Display all movies
            System.out.println("6. Show rental history");  // Display rental history
            System.out.println("7. Exit");                 // Exit program
            System.out.print("Choose: ");
            int choice = sc.nextInt(); // Read user menu choice
            sc.nextLine(); // Consume newline character after int input

            // ===============================
            // SWITCH CASE: Handle user choice
            // ===============================
            switch (choice){
                case 1:
                    // ===============================
                    // USER INPUT: Add new customer
                    // ===============================
                    System.out.println("\n--- Welcome New customer ---");
                    System.out.println("Customer Name: ");
                    String newCustomer = sc.nextLine(); // Read customer name
                    // Data flow: newCustomer string → customers HashSet
                    if(customers.add(newCustomer)){
                        System.out.println("Customer '"+ newCustomer +"' added sucessfully.");
                    }else{
                        System.out.println("Customer already exists.");
                    }
                    break;

                case 2:
                    // ===============================
                    // USER INPUT: Add new movie
                    // ===============================
                    System.out.println("Enter Movie title");
                    String newTitle = sc.nextLine(); // Read movie title

                    System.out.println("Is it digital or physical ? (d/p)");
                    String type = sc.nextLine(); // Read movie type

                    // Data flow: newTitle/type → new Movie object → movieList
                    if(type.equalsIgnoreCase("d")){
                        movieList.add(new DigitalMovie(newTitle)); // Add digital movie
                    }else if(type.equalsIgnoreCase("p")){
                        movieList.add(new PhysicalMovie(newTitle)); // Add physical movie
                    }else{
                        System.out.println("Movie title/format invalid! movie not added to to list !");
                    }
                    break;

                case 3:
                    // ===============================
                    // RENT MOVIES WITH EXCEPTION HANDLING
                    // ===============================
                    System.out.println("\n --- Rent a Movie ---");

                    System.out.println("Enter customer Name: ");
                    String customerName = sc.nextLine(); // Read customer name

                    // Data flow: customerName string → customers HashSet (lookup)
                    if(!customers.contains(customerName)){
                        System.out.println("❌ Customer not found!");
                        break;
                    }

                    // Print available movies (not rented)
                    System.out.println("Available Movies: ");
                    for(int i =0; i<movieList.size(); i++){
                        if(!movieList.get(i).isRented){
                            System.out.println(i + ". "+ movieList.get(i).title);
                        }
                    }

                    System.out.println("Enter movie index for rent");
                    int movieIndex = sc.nextInt(); // Read movie index
                    sc.nextLine(); // Consume newline

                    // Validate movie index
                    if(movieIndex < 0 || movieIndex >= movieList.size()){
                        System.out.println("❌ Invalid movie choice!");
                        break;
                    }

                    Movie chosenMovie = movieList.get(movieIndex); // Get chosen movie object from movieList
                    try{
                        // Try to rent movie (may throw RentalException)
                        chosenMovie.rent(); // Updates isRented and daysRented in Movie object
                        // Data flow: customerName → rentals HashMap → List<Movie>
                        rentals.putIfAbsent(customerName, new ArrayList<>()); // Create list if not present
                        rentals.get(customerName).add(chosenMovie); // Add movie to customer's rental list
                        // Data flow: rental event string → rentalHistory LinkedList
                        rentalHistory.add(customerName + " rented "+chosenMovie.title);
                    } catch(RentalException e){
                        // Exception handling: display error if rental fails
                        System.out.println("❌ Error: " +e.getMessage());
                    }
                    break;

                case 4:
                    // ===============================
                    // RETURN A MOVIE
                    // ===============================
                    System.out.println("\n--- Return a Movie ---");

                    System.out.println("Enter Customer Name: ");
                    String returningCustomer = sc.nextLine(); // Read customer name

                    // Data flow: returningCustomer → rentals HashMap → List<Movie>
                    List<Movie> rentedMovies = rentals.get(returningCustomer);
                    if(rentedMovies == null || rentedMovies.isEmpty()){
                        System.out.println("❌ No movies rented by this customer.");
                        break;
                    }

                    // Display customer's rented movies
                    System.out.println("Rented Movies: ");
                    for(int i = 0; i < rentedMovies.size(); i++){
                        System.out.println(i + ". " + rentedMovies.get(i).title);
                    }

                    System.out.println("Enter movie index to return: ");
                    int returnIndex = sc.nextInt(); // Read index to return
                    sc.nextLine(); // Consume newline

                    // Validate return index
                    if(returnIndex < 0 || returnIndex >= rentedMovies.size()){
                        System.out.println("❌ Invalid choice!");
                        break;
                    }

                    Movie movieToReturn = rentedMovies.get(returnIndex); // Get movie object to return
                    //ask user to input number of days
                    System.out.println("Enter the Number of days: ");
                    int dayskept = sc.nextInt();
                    sc.nextLine();
                    movieToReturn.addDays(dayskept);

                    //movieToReturn.addDays(daysKept); // Simulate 4 days rental for late fee

                    try{
                        // Try to return movie (may throw RentalException)
                        movieToReturn.returnItems(); // Updates isRented in Movie object
                        rentedMovies.remove(movieToReturn); // Remove movie from customer's rental list
                        rentalHistory.add(returningCustomer + " returned " + movieToReturn.title); // Add event to history
                    }catch(RentalException e){
                        System.out.println("❌ Error: " +e.getMessage());
                    }
                    break;

                case 5:
                    // ===============================
                    // SHOW ALL MOVIES
                    // ===============================
                    System.out.println("\n--- All Movies ---");
                    // Data flow: movieList → stream → lambda → formatted string → print
                    movieList.stream()
                        // Lambda: formats each movie's title and rental status
                        .map(m -> m.title + (m.isRented ? " (Rented)" : " (Available)"))
                        // forEach: prints each formatted string
                        .forEach(System.out::println);
                    // Why lambda? Concise, readable, and functional way to process and display data
                    break;

                case 6:
                    // ===============================
                    // SHOW RENTAL HISTORY
                    // ===============================
                    System.out.println("\n--- Rental History(Sorted) ---");
                    // Data flow: rentalHistory → stream → sorted → print
                    rentalHistory.stream()
                        // sorted(): sorts history alphabetically
                        .sorted()
                        // forEach: prints each event
                        .forEach(System.out::println);
                    // Why lambda? Efficiently processes and displays history in a single chain
                    break;

                case 7:
                    // ===============================
                    // EXIT
                    // ===============================
                    running = false; // End main loop
                    System.out.println("Thank you for using the Movie Rental System Exiting!");
                    break;

                default:
                    // Handle invalid menu choice
                    System.out.println("❌ Invalid choice!");
            }
        }
    }
}
