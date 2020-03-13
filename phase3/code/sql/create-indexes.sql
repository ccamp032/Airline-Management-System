CREATE INDEX index_reservation_status
ON Reservation
USING BTREE
(status);

CREATE INDEX index_reservation_cid
ON Reservation
USING BTREE
(cid);

CREATE INDEX index_reservation_fid
ON Reservation
USING BTREE
(fid);

--------------------------------------------

CREATE INDEX index_plane_seats
ON Plane
USING BTREE
(seats);

CREATE INDEX index_plane_id
ON Plane
USING BTREE
(id);

CREATE INDEX index_flightinfo_fid
ON FlightInfo
USING BTREE
(flight_id);

CREATE INDEX index_flightinfo_pid
ON FlightInfo
USING BTREE
(plane_id);

CREATE INDEX index_flight_num_sold
ON Flight
USING BTREE
(num_sold);

CREATE INDEX index_flight_fnum
ON Flight
USING BTREE
(fnum);

CREATE INDEX index_flight_act_depart_date
ON Flight
USING BTREE
(actual_departure_date);

--------------------------------------------

CREATE INDEX index_repairs_rid
ON Repairs
USING BTREE
(rid);

CREATE INDEX index_repairs_pid
ON Repairs
USING BTREE
(plane_id);