# CMPE275Lab2
 Flight Reservation System through REST API, Persistence, and Transactions.
 Cloud Service Engine: Amazon EC2
 
## Team Members:
- Hanish Punamiya
- Saketh Reddy Banda
- Shilpi Soni
- Spencer Siu
 
## Requirements:
- Each passenger can make one or more reservations. Time overlap between flights is not allowed among any of their reservations.
- Each reservation may consist of one or more flights.
- Each flight can carry one or more passengers.
- Each flight uses one plane, which is an embedded object with four fields mapped to the corresponding four columns in the airline table.
- The total amount of passengers cannot exceed the capacity of a plane.
- When a passenger is deleted, all their pending reservations are automatically canceled.
- A flight cannot be deleted if it needs to carry at least one passenger.

