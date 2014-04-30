This application is a multi-user Battleship game. There are two main stages of play: 

1. Board set-up: Each player must set up their respective board by selecting locations for
	different sizes of ships. This is done by selecting one square, three adjacent squares,
	and five adjacent squares (corresponding to small, medium, and large ships, respectively).
	Once the appropriate squares have been selected for one ship, the user must select 
	"Add Ship" on the main GUI, which adds one ship at a time.

2. Main game play: Each player now takes turns firing missiles at one location on their
	opponent's board. The missile will either hit the other players' ship, miss it, or sink
	the entire ship. A ship is "sunk" once all of the squares that it occupies have been
	hit by a missile. The first player to sink all of their opponent's ships is declared the
	winner.

USE:
	In order to begin play, one player must select File -> New Game. The second player must
	then select File -> Join Game and enter the IP address of the first player.	

	The use of JMenuBars enables users to gather more information about how to play the 
	game by selecting the "help" option. This provides information about the meaning of
	each icon used in the game, rules, and how to play. The "file" option allows for the user
	to either join a game or start a new game. When the user starts a new game, they must	
	enter the IP address of the computer that their opponent is using. Thus, the two users
	can now compete in a game of Battleship!

	BE SURE TO CLICK FILE -> EXIT WHEN THE GAME IS OVER OR WHEN ONE PLAYER QUITS!!! THIS IS
	CRITICAL TO ENSURE THAT ALL SOCKETS ARE CLOSED PROPERLY.

ICONS:

	Splash: A missile was fired, but missed a ship.
	
	Red circle: A missile was fired and hit a ship, but the ship is not sunk.

	Flame: A missile was fired and hit a ship, and the entire ship is now sunk.

	Green check-mark (only during stage 1): The square has been selected for the placement
		of a ship.