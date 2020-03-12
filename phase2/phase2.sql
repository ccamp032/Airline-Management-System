-- Kyle Dean, Christian Campos
-- CS166 Project Phase 2
-- Group - 166th Database

set client_min_messages="warning";

DROP TABLE IF EXISTS plane, pilot, flight, schedule, reservation, waitlisted, confirmed,
                     reserved, customer, plane, technician, repair_request, uses, has, repairs CASCADE;

/* Assumption - 'plane' does not have a foreign key since is has a one-to-many relation with flight
                and many-to-many with pilot */
CREATE TABLE plane ( ID text NOT NULL,
                     model text NOT NULL,
                     make text NOT NULL,
                     age text,
                     num_seats integer NOT NULL,
                     PRIMARY KEY(ID));

/* Assumption - 'pilot' does not have a foreign key since is has a one-to-many relation with flight
                and many-to-many with plane */
CREATE TABLE pilot ( ID text NOT NULL,
                     pilot_name text NOT NULL,
                     nationality text,
                     PRIMARY KEY(ID));

/* Assumption -  'flight' has two foreign keys; one from pilot and one from plane. Both foreign keys
                 have 'ON DELETE NO ACTION' because of the participation constraint */
CREATE TABLE flight ( flight_num text NOT NULL,
                      cost float NOT NULL,
                      num_sold integer NOT NULL,
                      num_stops integer NOT NULL,
                      actual_arrive_date date NOT NULL,
                      actual_arrive_time time NOT NULL,
                      actual_depart_date date NOT NULL,
                      actual_depart_time time NOT NULL,
                      source text,
                      destination text NOT NULL,
                      pilot_id text NOT NULL,
                      plane_id text NOT NULL,
                      PRIMARY KEY(flight_num),
                      FOREIGN KEY (pilot_id) REFERENCES pilot(ID) ON DELETE NO ACTION,
                      FOREIGN KEY (plane_id) REFERENCES plane(ID) ON DELETE NO ACTION);

/* Assumption - 'schedule' has the flight number attribute from the flight entity
                since it is a weak entity. The foreign key has 'ON DELETE CASCADE'
                because of the participation constraint */
CREATE TABLE schedule ( flight_num text NOT NULL,
                        day text NOT NULL,
                        depart_time time NOT NULL,
                        arrive_time time NOT NULL,
                        PRIMARY KEY(flight_num),
                        FOREIGN KEY (flight_num) REFERENCES flight(flight_num) ON DELETE CASCADE);

------------------------------------------------------------------------------------------

/* Assumption - 'reservation' has no foreign keys since it has a many-to-many relationship
                with customer and flight */
CREATE TABLE reservation ( Rnum text NOT NULL,
                           PRIMARY KEY(Rnum));

/* Assumption - part of the reservation ISA hierarchy */
CREATE TABLE waitlisted ( Rnum text NOT NULL,
                          PRIMARY KEY(Rnum),
                          FOREIGN KEY (Rnum) REFERENCES reservation(Rnum));

/* Assumption - part of the reservation ISA hierarchy */
CREATE TABLE confirmed ( Rnum text NOT NULL,
                         PRIMARY KEY(Rnum),
                         FOREIGN KEY (Rnum) REFERENCES reservation(Rnum));

/* Assumption - part of the reservation ISA hierarchy */
CREATE TABLE reserved ( Rnum text NOT NULL,
                        PRIMARY KEY(Rnum),
                        FOREIGN KEY (Rnum) REFERENCES reservation(Rnum));

------------------------------------------------------------------------------------------

/* Assumption - 'customer' has no foreign key since it has a many-to-many relationship
                with flight and reservation */
CREATE TABLE customer ( ID text NOT NULL,
                        first_name text NOT NULL,
                        last_name text NOT NULL,
                        gender text,
                        data_of_birth date NOT NULL,
                        customer_address text NOT NULL,
                        contact_num text NOT NULL,
                        ZIP_code text NOT NULL,
                        PRIMARY KEY(ID));

/* Assumption - 'technician' has no foreign key since it has a many-to-many relationship 
                with plane */
CREATE TABLE technician ( ID text NOT NULL,
                          PRIMARY KEY(ID));

------------------------------------------------------------------------------------------

/* Assumption - 'repair-request' has 3 foreign keys because of the many-to-many relationship
                with pilot and the connection to plane and technician */
CREATE TABLE repair_request ( pilot_id text NOT NULL,
                              plane_id text NOT NULL,
                              technician_id text NOT NULL,
                              repair_request_ID text NOT NULL,
                              PRIMARY KEY(pilot_id, plane_id, technician_id),
                              FOREIGN KEY (pilot_id) REFERENCES pilot(ID),
                              FOREIGN KEY (plane_id) REFERENCES plane(ID),
                              FOREIGN KEY (technician_id) REFERENCES technician(ID));

/* Assumption - 'uses' has 3 foreign keys since it is a ternary relationship between flight,
                pilot, and plane */
CREATE TABLE uses ( flight_num text NOT NULL,
                    pilot_id text NOT NULL,
                    plane_id text NOT NULL,
                    PRIMARY KEY(flight_num, pilot_id, plane_id),
                    FOREIGN KEY (flight_num) REFERENCES flight(flight_num),
                    FOREIGN KEY (pilot_id) REFERENCES pilot(ID),
                    FOREIGN KEY (plane_id) REFERENCES plane(ID));

/* Assumption - 'has' has 3 foreign keys since it is a ternary relationship between flight,
                customer, and reservation */
CREATE TABLE has ( flight_num text NOT NULL,
                   customer_id text NOT NULL,
                   Rnum text NOT NULL,
                   PRIMARY KEY (flight_num, customer_id, Rnum),
                   FOREIGN KEY (flight_num) REFERENCES flight(flight_num),
                   FOREIGN KEY (customer_id) REFERENCES customer(ID),
                   FOREIGN KEY (Rnum) REFERENCES reservation(Rnum));

/* Assumption - 'repairs' has 3 foreign keys because of the many-to-many relationship
                between plane and technician as well as the connection to pilot */
CREATE TABLE repairs ( plane_id text NOT NULL,
                       technician_id text NOT NULL,
                       pilot_id text NOT NULL,
                       repair_date date NOT NULL,
                       repair_code text NOT NULL,
                       PRIMARY KEY(plane_id, technician_id, pilot_id),
                       FOREIGN KEY (plane_id) REFERENCES plane(ID),
                       FOREIGN KEY (technician_id) REFERENCES technician(ID),
                       FOREIGN KEY (pilot_id) REFERENCES pilot(ID) ON DELETE NO ACTION);