package creature;

import game.GameEngine;

public class Location implements Comparable <Location> {

    public final int lineNumber;
    public final int position;

    public Location(int lineNumber, Integer position) {
        this.lineNumber = lineNumber;
        this.position = position;
    }

    public Location left() throws Exception {
        if (this.position == 0) throw new Exception("out of bound");
        return new Location(this.lineNumber, this.position - 1);
    }
    public Location right() throws Exception {
        if (this.position + 1 == GameEngine.getCurrentGameEngine().getLength()) throw new Exception("out of bound");
        return new Location(this.lineNumber, this.position + 1);
    }
    public Location nextLocation(int direction) throws Exception {
        if (direction == 1) return this.right();
        else if (direction == -1) return this.left();
        return this;
    }
    public Location moveBy(int dx, int dy) throws Exception {
        if (GameEngine.getCurrentGameEngine().locationChecker(lineNumber + dx, position + dy)) {
            throw new Exception("out of bound"); 
        }
        return new Location(lineNumber + dx, position + dy);
    }

    public boolean equals(Object location) {
        if (location instanceof Location) {
            return lineNumber == ((Location) location).lineNumber && position == ((Location) location).position;
        }
        return false;
    }

    @Override
    public int compareTo(Location location) {
        if (position == location.position) return lineNumber - location.lineNumber;
        return position - location.position;
    }

	public int position2() {
		//return position * getFRAME() * 2 + getFRAME();
        return (position/GameEngine.getFRAME() - 1) / 2;
    }
}