# tmee - TicketMaven Web version
This project is a web version of a closed-source Ticketing Program called TicketMaven. The closed source version is a 13+ year old Java/Swing desktop app
that automates the ticketing process used by some private communities with their own theaters.

### Why????

Most communities will do fine with one of the many off the shelf ticketing programs, including various web-based sevices. These programs have one weakness that a few communities can't handle. That is the unfair nature of first-come first-serve ticketing. For web-based programs, the best seats are gone within minutes, purchased by the same small number of users who wait for the tickets to go on sale and buy the best seats for themselves and friends. The rest of the purchasers get online whenever they can and find they are getting crappy seats for the entire season of shows. For programs that only support a manual purchasing process, the same problem occurs when certain customers stake out the box office in long lines to get the best seats.

TicketMaven only exists due to the communities that simply couldn't handle this kind of process. TicketMaven follows the following process:

1. Users enter "requests" for tickets at their leisure.
2. If desired, the requests can be considered "unpaid" until payment is received and then be marked as "paid". The program does not handle payment. Payment would be collected manually in some way.
3. The program administrator runs ticket lotteries which assign the actual tickets based on the requests. Tickets are not assigned at random. The customers who have gotten the worst seats in the past will get the best seats in the lottery. The program allows the administrator to set up auditorium models with a "goodness" value for each seat. The program keeps track of the quality of tickets that each customer has gotten in the past.
4. The users can print their tickets on paper, or if desired, the administrator can print all of the tickets to standard business card stock and distribute them (some communities prefer the formality of traditional stiff, card-board-like tickets).

The program also models special needs seats that are reserved for customers with various special needs, such as vision, hearing, mobility problems, etc... These special needs types not pre-defined, and are created as needed by the administrator.

### Implementation

The project is implemented using Primefaces on top of JEE since that is what the author is familiar with and also since the project started some time ago, in parallel with the desktop version, before many of the newer frameworks were a thing. 


