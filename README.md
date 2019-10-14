# tmee - TicketMaven Web version
This project is a web version of a closed-source Ticketing Program called TicketMaven. The closed source version is a 13+ year old Java/Swing desktop app
that automates the ticketing process used by some private communities with their own theaters.

### Why????

Most communities will do fine with one of the many off the shelf ticketing programs, including various web-based sevices. These programs have one weakness that a few communities can't handle. That is the unfair nature of first-come first-serve ticketing. For web-based programs, the best seats are gone within minutes, purchased by the same small number of users who wait for the tickets to go on sale and buy the best seats for themselves and friends. The rest of the purchasers get online whenever they can and find they are getting crappy seats for the entire season of shows. For programs that only support a manual purchasing process, the same problem occurs when certain customers stake out the box office in long lines to get the best seats.

TicketMaven only exists due to the communities that simply couldn't handle this kind of process. TicketMaven follows the following process:

1. Users enter "requests" for tickets at their leisure.
2. The requests are considered "unpaid" until payment is received and then be marked as "paid". 
3. The program administrator runs ticket lotteries which assign the actual tickets based on the requests. Tickets are not assigned at random. The customers who have gotten the worst seats in the past will get the best seats in the lottery. The program allows the administrator to set up auditorium models with a "goodness" value for each seat. The program keeps track of the quality of tickets that each customer has gotten in the past.
4. The users can print their tickets on paper, or if desired, the administrator can print all of the tickets to standard business card stock and distribute them (some communities prefer the formality of traditional stiff, card-board-like tickets).

The program also models special needs seats that are reserved for customers with various special needs, such as vision, hearing, mobility problems, etc... These special needs types not pre-defined, and are created as needed by the administrator.

**The web version is still incomplete and lacks some of the in-depth features of the desktop version.**

**Even when completed, communities can't just take this software and run it. Managing a web server is not trivial. A local nerd will be necessary. See the Support section below.**

### Design

A lot of features are based on the desktop version. The desktop help is [here](https://github.com/mikeberger/tmee/desktop_help/index.html)
The desktop help can serve as a design reference of sorts. A "Lite" version of the desktop version will be available at some point, just as reference code.

### Implementation

The project is implemented using Primefaces on top of JEE since that is what the author is familiar with and also since the project started some time ago, in parallel with the desktop version, before many of the newer frameworks were a thing. 

The web version is multi-tenant, meaning that a single running deployment can simulataneously support multiple communities. Each community would not have permission to see the data from any other, nor would a community be aware of the existence of the other communities.

### Licensing

This web version is free and open source, released under the GPL.

### Support

If there are communities with residents who are technically knowledgeable enough to manage a private web server or deploy the app to a JEE hosting site, then they might be good candidates to run the web version (once the software is ready). If there are residents who are capable of contributing code to the project, that would be welcome too. For a single community, the software could run on a single spare PC or even a raspberry pi.

If there isn't any interest, then writing the web version will just be something enjoyable to do and it will go nowhere.


