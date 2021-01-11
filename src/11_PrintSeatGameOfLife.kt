import complexity.GameOfLife
import complexity.play
import utils.fileLines

fun main() = fileLines("src/11_SeatingSystem.txt") { it }
    .map { GameOfLife(it.map { row -> row.split("") }, ::seatRules) }
    .first()
    .play()
