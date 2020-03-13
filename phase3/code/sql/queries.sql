-- Query 7

SELECT P.id, count(R.rid)
FROM Plane P, Repairs R 
WHERE P.id = R.plane_id
GROUP BY P.id
ORDER BY count DESC;

--Query 8

SELECT EXTRACT (year FROM R.repair_date) as "Year", count(R.rid)
FROM repairs R
GROUP BY "Year"
ORDER BY count ASC;
