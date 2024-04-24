package LPrice;

import robocode.*;
import java.awt.Color;
import java.lang.Math;

public class LukeRobot extends Robot {
	
	double bltPwr;
	boolean targetLocated;
	double confidenceLevel;
	boolean hasDanced;

		
    public void run() {
        setAdjustRadarForRobotTurn(true);
		bltPwr = 3;
		targetLocated = false;
		hasDanced = false;
		
        while (true) {
			if(Math.random() > 0.5){
			back(50);
			} else {
			ahead(50);
			}
            if (!targetLocated){
			turnGunRight(360);
			} else {
				turnGunRight(20);
				turnGunLeft(20);
			}
			//System.out.println("Confidence Level: " + confidenceLevel);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        if(e.getEnergy() == 0 && getOthers() == 1 && !hasDanced){
			//if the enemy robot has zero power, and there are no others, do a victory dance then blast em'
			victoryDance();
		} else {

		double absBearing = Math.toRadians(getHeading()) + e.getBearingRadians();
        double enemyBearingDeg = e.getBearing();
		double enemyDistance = e.getDistance();
		double enemyHeading = e.getHeadingRadians();
        double enemyVelocity = e.getVelocity();


		targetLocated = true;

		if(!(enemyBearingDeg >= 45 && enemyBearingDeg <= 135) || !(enemyBearingDeg >= 225 && enemyBearingDeg <= 315)){
			if(enemyBearingDeg < 180){
				setAdjustRadarForRobotTurn(false);
				turnRight(enemyBearingDeg - 90);
				setAdjustRadarForRobotTurn(true);
			} else {
				setAdjustRadarForRobotTurn(false);
				turnLeft(360 - enemyBearingDeg - 90);
				setAdjustRadarForRobotTurn(true);
			}
		}
		
		
		confidenceLevel = (((1414-enemyDistance)/1414));
		if(enemyVelocity == 0){confidenceLevel = 1.00;}

		System.out.println("Conf: " + confidenceLevel);
		
        // Calculate the predicted future x, y position of the enemy
        double x = getX() + enemyDistance * Math.sin(absBearing);
        double y = getY() + enemyDistance * Math.cos(absBearing);

        // Calculate the time it takes for the bullet to travel to the predicted position
        double bulletTime = enemyDistance / (20 - 3 * (bltPwr*confidenceLevel));

        // Predict the future x, y position of the enemy
        x += bulletTime * enemyVelocity * Math.sin(enemyHeading);
        y += bulletTime * enemyVelocity * Math.cos(enemyHeading);

        // Calculate the angle to the predicted future position
        double turnAngle = Math.toDegrees(Math.atan2(x - getX(), y - getY()));

        // Turn the gun to the calculated angle
        turnGunRight(normalizeRelativeAngle(turnAngle - getGunHeading()));
		
        // Fire at the predicted position
        fire((bltPwr*confidenceLevel));
		
		targetLocated = false;
    } }

    private double normalizeRelativeAngle(double angle) {
        while (angle > 180) {
            angle -= 360;
        }
        while (angle < -180) {
            angle += 360;
        }
        return angle;
    }

    public void onHitByBullet(HitByBulletEvent e) {
        back(30);
    }

    public void onHitWall(HitWallEvent e) {
        
    }
	
	public void victoryDance(){
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForRobotTurn(true);
		turnLeft(180);
		turnRight(180);
		ahead(100);
		turnLeft(180);
		turnRight(180);
		back(100);
		turnLeft(180);
		turnRight(180);
		turnRight(90);
		ahead(50);
		turnLeft(180);
		turnRight(180);
		back(50);
		setAdjustGunForRobotTurn(false);
		setAdjustRadarForRobotTurn(false);
		//Prevent me from getting trapped in infinite dance mode, even though the other robot would die before me eventually
		hasDanced = true;
	}
}
