# tmee - TicketMaven JEE version
This project is a web version of a closed-source Ticketing Program called TicketMaven. The closed source version is a 13+ year old Java/Swing desktop app
that automates the ticketing process used by some private communities with their own theaters.

The project is implemented using Primefaces on top of JEE since that is what I am familiar with and also since I started toying with this project before many of the newer frameworks were a thing. It would also make sense to get it working as a thorntail uber-jar.

This application (and also the desktop app), lack many features that exist in other ticketing apps. The main feature for this app is the ticket lottery, which assigns tickets fairly, based on the "quality" of prior tickets that each customer has received. This avoids having customers fighting to be the first purchaser, as happens with first-come first served systems.
