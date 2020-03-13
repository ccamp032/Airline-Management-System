/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */

public class DBproject {
	// reference to physical database connection
	private Connection _connection = null;
	static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	public DBproject(String dbname, String dbport, String user, String passwd) throws SQLException {
		System.out.print("Connecting to database...");
		try {
			// constructs the connection URL
			String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
			System.out.println("Connection URL: " + url + "\n");

			// obtain a physical connection
			this._connection = DriverManager.getConnection(url, user, passwd);
			System.out.println("Done");
		} catch (Exception e) {
			System.err.println("Error - Unable to Connect to Database: " + e.getMessage());
			System.out.println("Make sure you started postgres on this machine");
			System.exit(-1);
		}
	}

	/**
	 * Method to execute an update SQL statement. Update SQL instructions includes
	 * CREATE, INSERT, UPDATE, DELETE, and DROP.
	 * 
	 * @param sql the input SQL string
	 * @throws java.sql.SQLException when update failed
	 */
	public void executeUpdate(String sql) throws SQLException {
		// creates a statement object
		Statement stmt = this._connection.createStatement();

		// issues the update instruction
		stmt.executeUpdate(sql);

		// close the instruction
		stmt.close();
	}// end executeUpdate

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT). This method
	 * issues the query to the DBMS and outputs the results to standard out.
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQueryAndPrintResult(String query) throws SQLException {
		// creates a statement object
		Statement stmt = this._connection.createStatement();

		// issues the query instruction
		ResultSet rs = stmt.executeQuery(query);

		/*
		 * obtains the metadata object for the returned result set. The metadata
		 * contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData();
		int numCol = rsmd.getColumnCount();
		int rowCount = 0;

		// iterates through the result set and output them to standard out.
		boolean outputHeader = true;
		while (rs.next()) {
			if (outputHeader) {
				for (int i = 1; i <= numCol; i++) {
					System.out.print(rsmd.getColumnName(i) + "\t");
				}
				System.out.println();
				outputHeader = false;
			}
			for (int i = 1; i <= numCol; ++i)
				System.out.print(rs.getString(i) + "\t");
			System.out.println();
			++rowCount;
		} // end while
		stmt.close();
		return rowCount;
	}

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT). This method
	 * issues the query to the DBMS and returns the results as a list of records.
	 * Each record in turn is a list of attribute values
	 * 
	 * @param query the input query string
	 * @return the query result as a list of records
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public List<List<String>> executeQueryAndReturnResult(String query) throws SQLException {
		// creates a statement object
		Statement stmt = this._connection.createStatement();

		// issues the query instruction
		ResultSet rs = stmt.executeQuery(query);

		/*
		 * obtains the metadata object for the returned result set. The metadata
		 * contains row and column info.
		 */
		ResultSetMetaData rsmd = rs.getMetaData();
		int numCol = rsmd.getColumnCount();
		int rowCount = 0;

		// iterates through the result set and saves the data returned by the query.
		boolean outputHeader = false;
		List<List<String>> result = new ArrayList<List<String>>();
		while (rs.next()) {
			List<String> record = new ArrayList<String>();
			for (int i = 1; i <= numCol; ++i)
				record.add(rs.getString(i));
			result.add(record);
		} // end while
		stmt.close();
		return result;
	}// end executeQueryAndReturnResult

	/**
	 * Method to execute an input query SQL instruction (i.e. SELECT). This method
	 * issues the query to the DBMS and returns the number of results
	 * 
	 * @param query the input query string
	 * @return the number of rows returned
	 * @throws java.sql.SQLException when failed to execute the query
	 */
	public int executeQuery(String query) throws SQLException {
		// creates a statement object
		Statement stmt = this._connection.createStatement();

		// issues the query instruction
		ResultSet rs = stmt.executeQuery(query);

		int rowCount = 0;

		// iterates through the result set and count nuber of results.
		if (rs.next()) {
			rowCount++;
		} // end while
		stmt.close();
		return rowCount;
	}

	/**
	 * Method to fetch the last value from sequence. This method issues the query to
	 * the DBMS and returns the current value of sequence used for autogenerated
	 * keys
	 * 
	 * @param sequence name of the DB sequence
	 * @return current value of a sequence
	 * @throws java.sql.SQLException when failed to execute the query
	 */

	public int getCurrSeqVal(String sequence) throws SQLException {
		Statement stmt = this._connection.createStatement();

		ResultSet rs = stmt.executeQuery(String.format("Select currval('%s')", sequence));
		if (rs.next())
			return rs.getInt(1);
		return -1;
	}

	/**
	 * Method to close the physical connection if it is open.
	 */
	public void cleanup() {
		try {
			if (this._connection != null) {
				this._connection.close();
			} // end if
		} catch (SQLException e) {
			// ignored.
		} // end try
	}// end cleanup

	/**
	 * The main execution method
	 * 
	 * @param args the command line arguments this inclues the <mysql|pgsql> <login
	 *             file>
	 */
	public static void main(String[] args) {
		if (args.length != 3) {
			System.err.println("Usage: " + "java [-classpath <classpath>] " + DBproject.class.getName()
					+ " <dbname> <port> <user>");
			return;
		} // end if

		DBproject esql = null;

		try {
			System.out.println("(1)");

			try {
				Class.forName("org.postgresql.Driver");
			} catch (Exception e) {

				System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
				e.printStackTrace();
				return;
			}

			System.out.println("(2)");
			String dbname = args[0];
			String dbport = args[1];
			String user = args[2];

			esql = new DBproject(dbname, dbport, user, "");

			boolean keepon = true;
			while (keepon) {
				System.out.println("MAIN MENU");
				System.out.println("---------");
				System.out.println("1. Add Plane");
				System.out.println("2. Add Pilot");
				System.out.println("3. Add Flight");
				System.out.println("4. Add Technician");
				System.out.println("5. Book Flight");
				System.out.println("6. List number of available seats for a given flight.");
				System.out.println("7. List total number of repairs per plane in descending order");
				System.out.println("8. List total number of repairs per year in ascending order");
				System.out.println("9. Find total number of passengers with a given status");
				System.out.println("10. < EXIT");

				switch (readChoice()) {
					case 1:
						AddPlane(esql);
						break;
					case 2:
						AddPilot(esql);
						break;
					case 3:
						AddFlight(esql);
						break;
					case 4:
						AddTechnician(esql);
						break;
					case 5:
						BookFlight(esql);
						break;
					case 6:
						ListNumberOfAvailableSeats(esql);
						break;
					case 7:
						ListsTotalNumberOfRepairsPerPlane(esql);
						break;
					case 8:
						ListTotalNumberOfRepairsPerYear(esql);
						break;
					case 9:
						FindPassengersCountWithStatus(esql);
						break;
					case 10:
						keepon = false;
						break;
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		} finally {
			try {
				if (esql != null) {
					System.out.print("Disconnecting from database...");
					esql.cleanup();
					System.out.println("Done\n\nBye !");
				} // end if
			} catch (Exception e) {
				// ignored.
			}
		}
	}

	public static int readChoice() {
		int input;
		// returns only if a correct value is given.
		do {
			System.out.print("Please make your choice: ");
			try { // read the integer, parse it and break.
				input = Integer.parseInt(in.readLine());
				break;
			} catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			} // end try
		} while (true);
		return input;
	}// end readChoice

    //Input data into query based on user's input
	public static void inputData(DBproject esql, String query) {
		String input;
		do {
			try {
				input = in.readLine();
				if(input.equals("Y") || input.equals("y")) {
					try {
						esql.executeUpdate(query);
						System.out.println("\nData was inserted into the database!");
						break;
					}catch (Exception e) {
						System.err.println (e.getMessage());
						continue;
					}
				}
				else if(input.equals("N") || input.equals("n")) {
					System.out.println("\nData was NOT inserted into the database!");
					break;
				}
				else {
					System.out.println("\nYour input is invalid!");
					System.out.print("\nPlease enter (Y/N): ");
				}
			}catch (Exception e) {
				System.err.println (e.getMessage());
				continue;
			}
		}while (true);
		System.out.println("-----------------------------------------------------------------");
	}

	//Output data
	public static void outputData(DBproject esql, String query) {
		try {
			System.out.println("-----------------------------------------------------------------");
			esql.executeQueryAndPrintResult(query);
			System.out.println("-----------------------------------------------------------------");
		}catch (Exception e) {
			System.err.println (e.getMessage());
		}
	}

	public static String getReservationStatus() {
		String status;
		do {
			try {
				status = in.readLine();
				if(!status.equals("W") && !status.equals("R") && !status.equals("C"))
					throw new RuntimeException("Input only accepts the following: W, R, C");
				break;
			}catch (Exception e) {
				System.err.println (e.getMessage());
				continue;
			}
		}while (true);
		return status;
	}

	public static int getCustomerID() {
		int customerID;
		do {
			System.out.print("Input Customer ID: ");
			try {
				customerID = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		}while (true);
		return customerID;
	}

	public static int getFlightNumber() {
		int flightNum;
		do {
			System.out.print("Input Flight Number: ");
			try {
				flightNum = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		}while (true);
		return flightNum;
	}

	/////////////////////////////////////////////////////////////////////////////////////
	//// QUERY 1
	/////////////////////////////////////////////////////////////////////////////////////
	public static void AddPlane(DBproject esql) {//1
		int planeID, age, seats;
		String make, model;
		
		System.out.println("-----------------------------------------------------------------");

		//Get Plane ID
		do {
			System.out.print("Input Plane ID Number: ");
			try {
				planeID = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		}while (true);
		

		//Get make of plane
		do {
			System.out.print("Input Plane Make: ");
			try {
				make = in.readLine();
				if(make.length() <= 0 || make.length() > 32)
					throw new RuntimeException("Plane Make cannot be null or exceed 32 characters");
				break;
			}catch (Exception e) {
				System.err.println (e.getMessage());
				continue;
			}
		}while (true);
		
		//Get model of plane
		do {
			System.out.print("Input Plane Model: ");
			try {
				model = in.readLine();
				if(model.length() <= 0 || model.length() > 64)
					throw new RuntimeException("Plane Model cannot be null or exceed 64 characters");
				break;
			}catch (Exception e) {
				System.err.println (e.getMessage());
				continue;
			}
		}while (true);
		
		
		//Get age of plane
		do {
			System.out.print("Input Plane Age: ");
			try {
				age = Integer.parseInt(in.readLine());
				if(age < 0)
					throw new RuntimeException("Plane Age cannot be negative");
				break;
			}catch (NumberFormatException e) {
				System.out.println("Your input is invalid!");
				continue;
			}catch (Exception e) {
				System.err.println (e.getMessage());
				continue;
			}
		}while (true);
		

		//Get number of seats on plane...has to be between 0 and 500 inclusive
		do {
			System.out.print("Input Number of Plane Seats: ");
			try {
				seats = Integer.parseInt(in.readLine());
				if(age <= 0 || age >= 500)
					throw new RuntimeException("Number of Plane Seats cannot be less than or equal to 0 or greater than or equal to 500");
				break;
			}catch (NumberFormatException e) {
				System.out.println("Your input is invalid!");
				continue;
			}catch (Exception e) {
				System.err.println (e.getMessage());
				continue;
			}
		}while (true);

		System.out.println("\n---------------------------------------");
		System.out.println("|        Plane ID = " + planeID);
		System.out.println("|      Plane Make = " + make);
		System.out.println("|     Plane Model = " + model);
		System.out.println("|       Plane Age = " + age);
		System.out.println("| Number of Seats = " + seats);
		System.out.println("---------------------------------------");
		System.out.print("\nAre you sure you want to input the new data above into PLANE? (Y/N): ");
		String query = "INSERT INTO Plane (id, make, model, age, seats) VALUES (" + planeID + ", \'" + make + "\', \'" + model + "\', " + age + ", " + seats + ");";
		inputData(esql, query);
	}

	/////////////////////////////////////////////////////////////////////////////////////
	//// QUERY 2
	/////////////////////////////////////////////////////////////////////////////////////
	public static void AddPilot(DBproject esql) {// 2
		int pilotID;
		String name, nationality;

		System.out.println("-----------------------------------------------------------------");

		// Get Pilot ID
		do {
			System.out.print("Input Pilot ID Number: ");
			try {
				pilotID = Integer.parseInt(in.readLine());
				break;
			} catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		} while (true);

		// Get pilot name
		do {
			System.out.print("Input Pilot Name: ");
			try {
				name = in.readLine();
				if (name.length() <= 0 || name.length() > 128)
					throw new RuntimeException("Pilot Name cannot exceed 128 characters");
				break;
			} catch (Exception e) {
				System.err.println(e.getMessage());
				continue;
			}
		} while (true);

		// Get pilot nationality
		do {
			System.out.print("Input Pilot Nationality: ");
			try {
				nationality = in.readLine();
				if (nationality.length() <= 0 || nationality.length() > 24)
					throw new RuntimeException("Pilot Nationality cannot exceed 24 characters");
				break;
			} catch (Exception e) {
				System.err.println(e.getMessage());
				continue;
			}
		} while (true);

		System.out.println("\n---------------------------------------");
		System.out.println("|          Pilot ID = " + pilotID);
		System.out.println("|        Pilot Name = " + name);
		System.out.println("| Pilot Nationality = " + nationality);
		System.out.println("---------------------------------------");

		System.out.print("\nAre you sure you want to input the new data above into PILOT? (Y/N): ");
		String query = "INSERT INTO Pilot (id, fullname, nationality) VALUES (" + pilotID + ", \'" + name + "\', \'" + nationality + "\');";
		inputData(esql, query);
	}

	/////////////////////////////////////////////////////////////////////////////////////
	//// QUERY 3
	/////////////////////////////////////////////////////////////////////////////////////
	public static void AddFlight(DBproject esql) {//3
		// Given a pilot, plane and flight, adds a flight in the DB

		int flightNum, cost, sold, stops;
		String leave, arrival, destination, departure;
		LocalDate leaveDate;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		
		System.out.println("-----------------------------------------------------------------");

		flightNum = getFlightNumber();
		
		//Get flight cost
		do {
			System.out.print("Input Flight Cost: ");
			try {
				cost = Integer.parseInt(in.readLine());
				if(cost <= 0) //flight cost cannot be less than or equal to 0
					throw new RuntimeException("Flight Cost cannot be less than or equal to 0");
				break;
			}catch (NumberFormatException e) {
				System.out.println("Your input is invalid!");
				continue;
			}catch (Exception e) {
				System.err.println (e.getMessage());
				continue;
			}
		}while (true);
		
		//Get number of seats sold
		do {
			System.out.print("Input Number of Seats Sold: ");
			try {
				sold = Integer.parseInt(in.readLine());
				if(sold < 0) //seats sold cannot be less than 0
					throw new RuntimeException("Number of Seats Sold cannot be negative");
				break;
			}catch (NumberFormatException e) {
				System.out.println("Your input is invalid!");
				continue;
			}catch (Exception e) {
				System.err.println (e.getMessage());
				continue;
			}
		}while (true);
		
		//Get number of stops
		do {
			System.out.print("Input Number of Stops: ");
			try {
				stops = Integer.parseInt(in.readLine());
				if(stops < 0) //number of stops cannot be less than 0
					throw new RuntimeException("Number of Stops cannot be negative");
				break;
			}catch (NumberFormatException e) {
				System.out.println("Your input is invalid!");
				continue;
			}catch (Exception e) {
				System.err.println (e.getMessage());
				continue;
			}
		}while (true);
		
		//Get departure time
		do {
			System.out.print("Input Departure Time (YYYY-MM-DD hh:mm): ");
			try {
				leave = in.readLine();
				leaveDate = LocalDate.parse(leave, formatter);
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		}while (true);

		//Get arrival time
		do {
			System.out.print("Input Arrival Time (YYYY-MM-DD hh:mm) (Note Arrival Time cannot be before Departure Time): ");
			try {
				arrival = in.readLine();
				LocalDate arrivalDate = LocalDate.parse(arrival, formatter);
				if(!arrivalDate.isAfter(leaveDate))
					throw new RuntimeException();
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		}while (true);
		
		//Get flight destination
		do {
			System.out.print("Input Destination Airport: ");
			try {
				destination = in.readLine();
				if(destination.length() <= 0 || destination.length() > 5)
					throw new RuntimeException("Desination Airport cannot be null or exceed 5 characters");
				break;
			}catch (Exception e) {
				System.err.println (e.getMessage());
				continue;
			}
		}while (true);
		
		//Get flight departure
		do {
			System.out.print("Input Departure Airport: ");
			try {
				departure = in.readLine();
				if(departure.length() <= 0 || departure.length() > 5)
					throw new RuntimeException("Departure Airport cannot be null or exceed 5 characters");
				break;
			}catch (Exception e) {
				System.err.println (e.getMessage());
				continue;
			}
		}while (true);

		System.out.println("\n---------------------------------------");
		System.out.println("|        Flight Number = " + flightNum);
		System.out.println("|          Flight Cost = " + cost);
		System.out.println("| Number of Seats Sold = " + sold);
		System.out.println("|      Number of Stops = " + stops);
		System.out.println("|       Departure Time = " + leave);
		System.out.println("|         Arrival Time = " + arrival);
		System.out.println("|          Destination = " + destination);
		System.out.println("|            Departure = " + departure);
		System.out.println("---------------------------------------");

		System.out.print("\nAre you sure you want to input the new data above into FLIGHT? (Y/N): ");
		String query = "INSERT INTO Flight (fnum, cost, num_sold, num_stops, actual_departure_date, actual_arrival_date, arrival_airport, departure_airport) VALUES (" + flightNum + ", " + cost + ", " + sold + ", " + stops + ", \'" + leave + "\', \'" + arrival + "\', \'" + destination + "\', \'" + departure + "\');";
		inputData(esql, query);
	}

	/////////////////////////////////////////////////////////////////////////////////////
	//// QUERY 4
	/////////////////////////////////////////////////////////////////////////////////////
	public static void AddTechnician(DBproject esql) {//4
		int techID;
		String techName;
		
		System.out.println("-----------------------------------------------------------------");

		//Get technician ID
		do {
			System.out.print("Input Technician ID Number: ");
			try {
				techID = Integer.parseInt(in.readLine());
				break;
			}catch (Exception e) {
				System.out.println("Your input is invalid!");
				continue;
			}
		}while (true);
		
		//Get technician name
		do {
			System.out.print("Input Technician Name: ");
			try {
				techName = in.readLine();
				if(techName.length() <= 0 || techName.length() > 128)
					throw new RuntimeException("Technician Name cannot be null or exceed 128 characters");
				break;
			}catch (Exception e) {
				System.err.println (e.getMessage());
				continue;
			}
		}while (true);

		System.out.println("\n---------------------------------------");
		System.out.println("|   Technician ID = " + techID);
		System.out.println("| Technician Name = " + techName);
		System.out.println("---------------------------------------");
		System.out.print("\nAre you sure you want to input the new data below into TECHNICIAN? (Y/N): ");

		String query = "INSERT INTO Technician (id, full_name) VALUES (" + techID + ", \'" + techName + "\');";
		inputData(esql, query);
	}

	/////////////////////////////////////////////////////////////////////////////////////
	//// QUERY 5
	/////////////////////////////////////////////////////////////////////////////////////
	public static void BookFlight(DBproject esql) {//5
		// Given a customer and a flight that he/she wants to book, add a reservation to the DB

		int customerID, flightNum, reserve;
		String input, status, query;
		
		//Get customer ID
		customerID = getCustomerID();
		
		//Get flight number
		flightNum = getFlightNumber();
		
		try {
			query = "SELECT status\nFROM Reservation\nWHERE cid = " + customerID + " AND fid = " + flightNum + ";";
			
			//Run query to see if it exists
			if(esql.executeQueryAndPrintResult(query) == 0) {
				do {
					System.out.println("The Reservation with customer ID " + customerID + " and flight number " + flightNum + " does not exist. Would you like to book a reservation? (Y/N): ");
					try {
						input = in.readLine();
						if(input.equals("Y") || input.equals("y")) {

							//Input new reservation number
							do {
								System.out.print("Input NEW Reservation Number: ");
								try {
									reserve = Integer.parseInt(in.readLine());
									break;
								}catch (Exception e) {
									System.out.println("Your input is invalid!");
									continue;
								}
							}while (true);
							
							//Input new reservation status
							System.out.print("Input NEW Reservation Status: ");
							status = getReservationStatus();

							//Insert new data into database
							try {
								query = "INSERT INTO Reservation (rnum, cid, fid, status) VALUES (" + reserve + ", " + customerID + ", " + flightNum + ", \'" + status + "\');";
								esql.executeUpdate(query);
							}catch (Exception e) {
								System.err.println (e.getMessage());
							}
							
						}
						//If user enter something other than Y,y,N,n
						else if(!input.equals("N") || !input.equals("n")) {
							throw new RuntimeException("Your input is invalid!");
						}
						break;
					}catch (Exception e) {
						System.err.println (e.getMessage());
						continue;
					}
				}while (true);
			}
			//If reservation already exists...
			else {
				do{
					try{
						System.out.println("Would you like to update the reservation? (Y/N): ");
						input = in.readLine();
						if(input.equals("Y") || input.equals("y")) {

							//Input updated reservation status
							System.out.print("Input UPDATED Reservation Status: ");
							status = getReservationStatus();

							//Update existing data in the database
							try {
								query = "UPDATE Reservation SET status = \'" + status + "\' WHERE cid = " + customerID + " AND fid = " + flightNum + ";";
								esql.executeUpdate(query);
							}catch (Exception e) {
								System.err.println (e.getMessage());
							}
						}
						//If user enter something other than Y,y,N,n
						else if(!input.equals("N") || !input.equals("n")) {
							throw new RuntimeException("Your input is invalid!");
						}
						break;
					}catch (Exception e) {
						System.err.println (e.getMessage());
						continue;
					}
				}while (true);
			}
		}catch (Exception e) {
			System.err.println (e.getMessage());
		}
	}

	/////////////////////////////////////////////////////////////////////////////////////
	//// QUERY 6
	/////////////////////////////////////////////////////////////////////////////////////
	public static void ListNumberOfAvailableSeats(DBproject esql) {//6
		// For flight number and date, find the number of availalbe seats (i.e. total plane capacity minus booked seats )
	}

	/////////////////////////////////////////////////////////////////////////////////////
	//// QUERY 7
	/////////////////////////////////////////////////////////////////////////////////////
	public static void ListsTotalNumberOfRepairsPerPlane(DBproject esql) {//7
		// Count number of repairs per planes and list them in descending order

		String query = "SELECT P.id, count(R.rid)\nFROM Plane P, Repairs R\nWHERE P.id = R.plane_id\nGROUP BY P.id\nORDER BY count DESC;";
		outputData(esql, query);
	}

	/////////////////////////////////////////////////////////////////////////////////////
	//// QUERY 8
	/////////////////////////////////////////////////////////////////////////////////////
	public static void ListTotalNumberOfRepairsPerYear(DBproject esql) {//8
		// Count repairs per year and list them in ascending order

		String query = "SELECT EXTRACT (year FROM R.repair_date) as \"Year\", count(R.rid)\nFROM repairs R\nGROUP BY \"Year\"\nORDER BY count ASC;";
		outputData(esql, query);
	}
	
	/////////////////////////////////////////////////////////////////////////////////////
	//// QUERY 9
	/////////////////////////////////////////////////////////////////////////////////////
	public static void FindPassengersCountWithStatus(DBproject esql) {//9
		// Find how many passengers there are with a status (i.e. W,C,R) and list that number.
		int flightNum;
		String status;
		
		System.out.println("-----------------------------------------------------------------");

		//Get flight number
		flightNum = getFlightNumber();
		
		//Get passenger status (W,R,C)
		System.out.print("Input Passenger Status: ");
		status = getReservationStatus();

		String query = "SELECT COUNT(*)\nFROM Reservation\nWHERE fid = " + flightNum + " AND status = \'" + status + "\';";
		outputData(esql, query);
	}
}