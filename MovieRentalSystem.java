import java.util.*;
import java.util.stream.*;

// ===============================
// CUSTOM EXCEPTION
// ===============================

class RentalException extends Exception{
    public RentalException(String message){
        super(message);
    }
}


// ===============================
// INTERFACE
// ===============================

interface Rentable{
    void rent() throws RentalException;
    void returnItems() throws RentalException;

}

// ===============================
// ABSTRACT CLASS: Base class for Movies
// ===============================

abstract class Movie implements Rentable{
    protected String title;
    protected boolean isRented;
    protected int daysRented; // Track number of days movie has been rented

    public Movie(String title){
        this.title = title;
        this.isRented = false;
        this.daysRented = 0;
     }

     // ======= Overloaded Methods (Polymorphism) =======
    public void displayInfo(){System.out.println("Movie: "+ title);}
    public void displayInfo(String prefix){System.out.println(prefix+ " "+ title);}

    // Abstract method → each child class must define its rental fee
    public abstract double getRentalFee();

    public void rent() throws RentalException{
        if(!isRented){
            isRented = true;
            daysRented = 0;
            System.out.println(title + "has been rented");
        }else{
            throw new RentalException(title + "is already rented."); // Exception if already rented
        }


    }

    public void returnItems() throws RentalException{
        if(isRented){
            isRented = false;
            System.out.println(title+ " has been return total fee is: RM "+ calculateFee());
        }else{
            throw new RentalException(title+"was not rented.");
        }

    }

    // ======= Inherited Calculation Method =======
 // Calculates rental fee + late fee (RM 2 per extra day after 3 days)

 public double calculateFee(){
    double fee = getRentalFee();

    if(daysRented > 3){
        fee += (daysRented - 3) * 2; //RM 2 per late day

    }
    return fee;
 }

 // Simulate passing of days
 public void addDays(int days){this.daysRented += days;}

}

// ===============================
// CHILD CLASSES: Digital & Physical Movies
// ===============================
class DigitalMovie extends Movie{

    public DigitalMovie(String title){super(title);}

    @Override
    public double getRentalFee(){return 5.0;}
}


class PhysicalMovie extends Movie{
    public PhysicalMovie(String title){super(title);}

    @Override
    public double getRentalFee(){return 10.0;}
}
 
// ===============================
// MAIN CLASS
// ===============================
public class MovieRentalSystem{

    // Static helper method to calculate total fees for a list of movies
    public static double calculateTotalFees(List<Movie>movieList){
        return movieList.stream()
            .mapToDouble(m -> m.calculateFee()) // <- explicit lambda (also correct)
            .sum();
    }
    
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // ===============================
        // DATA STRUCTURES
        // ===============================
        // Array: store movies

        
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
       

          // HashSet: unique customers
          HashSet<String> customers = new HashSet<>(Arrays.asList("Alice", "Bob", "Charlie","Jack","Kim"));

          // LinkedList: rental history
          LinkedList<String> rentalHistory = new LinkedList<>();

          // HashMap: customer → list of rented movies
          HashMap<String, List<Movie>> rentals = new HashMap<>();



        boolean running  = true;
        while(running){
                System.out.println("\n--- Movie Rental System ---");
                System.out.println("1. Add a new customer");   // Create a customer object and store in list/set
                System.out.println("2. Add a new movie");      // Add movie to library (ArrayList / HashMap)
                System.out.println("3. Rent a movie");         // Rent a movie (updates rented status, fee calc)
                System.out.println("4. Return a movie");       // Return a rented movie, apply late fee if needed
                System.out.println("5. Show all movies");      // Use stream() to display all movies
                System.out.println("6. Show rental history");  // Show past rentals, using LinkedList or ArrayList
                System.out.println("7. Exit");                 // End program
                System.out.print("Choose: ");
                int choice = sc.nextInt();
                sc.nextLine();

            switch (choice){
                case 1:
                    // ===============================
                    // USER INPUT: Add new customer
                    // ===============================
                    System.out.println("\n--- Welcome New customer ---");
                    sc.nextLine();
                    System.out.println("Customer Name: ");
                    String newCustomer = sc.nextLine();
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
                    sc.nextLine();
                    System.out.println("Enter Movie title");
                    String newTitle = sc.nextLine();

                    sc.nextLine();
                    System.out.println("Is it digital or physical ? (d/p)");
                    String type = sc.nextLine();

                    if(type.equalsIgnoreCase("d")){
                        movieList.add(new DigitalMovie(newTitle));

                    }else if(type.equalsIgnoreCase("p")){
                        movieList.add(new PhysicalMovie(newTitle));

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
                        String customerName = sc.nextLine();

                        if(!customers.contains(customerName)){
                            System.out.println("❌ Customer not found!");
                            break;

                        }
                        
                        //printing out avaialable movies.
                        System.out.println("Available Movies: ");
                        for(int i =0; i<movieList.size(); i++){

                            if(!movieList.get(i).isRented){

                                System.out.println(i + ". "+ movieList.get(i).title);

                            }

                        }

                        System.out.println("Enter movie index for rent");
                        int movieIndex = sc.nextInt();
                        sc.nextLine();

                        if(movieIndex < 0 || movieIndex >= movieList.size()){

                              System.out.println("❌ Invalid movie choice!");
                              break;

                        }

                        Movie chosenMovie = movieList.get(movieIndex);
                        try{
                            chosenMovie.rent();
                            rentals.putIfAbsent(customerName, new ArrayList<>());
                            rentals.get(customerName).add(chosenMovie);
                            rentalHistory.add(customerName + " rented "+chosenMovie.title);
                        } catch(RentalException e){
                            System.out.println("❌ Error: " +e.getMessage());

                        }
                        break;

                    case 4:
                        // ===============================
                        // RETURN A MOVIE
                        // ===============================
                        System.out.println("\n--- Return a Movie ---");

                        System.out.println("Enter Customer Name: ");
                        String returningCustomer = sc.nextLine();

                        List<Movie> rentedMovies = rentals.get(returningCustomer);
                        if(rentedMovies == null || rentedMovies.isEmpty()){

                            System.out.println("❌ No movies rented by this customer.");
                            break;
                        }

                        //displaying rented movies
                        System.out.println("Rented Movies: ");
                        for(int i = 0; i < rentedMovies.size(); i++){
                            System.out.println(i + ". " + rentedMovies.get(i).title);
                        }

                        System.out.println("Enter movie index to return: ");
                        int returnIndex = sc.nextInt();
                        sc.nextLine();

                        if(returnIndex < 0 || returnIndex >= rentedMovies.size()){
                            System.out.println("❌ Invalid choice!");
                            break;

                        }

                    
                        Movie movieToReturn = rentedMovies.get(returnIndex);
                        movieToReturn.addDays(4);

                        try{
                            movieToReturn.returnItems();
                            rentedMovies.remove(movieToReturn);
                            rentalHistory.add(returningCustomer + " returned " + movieToReturn.title);

                        }catch(RentalException e){
                            System.out.println("❌ Error: " +e.getMessage());

                        }
                        break;

                    case 5:
                     // ===============================
                    // SHOW ALL MOVIES
                    // ===============================
                        System.out.println("\n--- All Movies ---");
                        movieList.stream()
                                 .map(m ->m.title + (m.isRented ? " (Rented)" : " (Available)"))
                                 .forEach(System.out::println);
                        break;

                    case 6:
                        // ===============================
                        // SHOW RENTAL HISTORY
                        // ===============================
                        System.out.println("\n--- Rental History(Sorted) ---");
                        rentalHistory.stream()
                                     .sorted()
                                     .forEach(System.out::println);
                        break;
                    case 7:
                        // ===============================
                        // EXIT
                        // ===============================
                        running = false;
                        System.out.println("Thank you for using the Movie Rental System Exiting!");
                        break;

                    default:
                        System.out.println("❌ Invalid choice!");
            }


        }

    }
}
