package LPrice;

import robocode.*;
import java.awt.Color;
import java.lang.Math;

public class LukeRobot extends Robot {
	
	double bltPwr;
	boolean targetLocated;

	
    public void run() {
        setAdjustRadarForRobotTurn(true);
		bltPwr = 2;
		targetLocated = false;

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
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double absBearing = Math.toRadians(getHeading()) + e.getBearingRadians();
        double enemyBearingDeg = e.getBearing();
		double distance = e.getDistance();
		double enemyHeading = e.getHeadingRadians();
        double enemyVelocity = e.getVelocity();
		
		targetLocated = true;

		if(!(enemyBearingDeg >= 50 && enemyBearingDeg <= 130) || !(enemyBearingDeg >= 230 && enemyBearingDeg <= 310)){
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
		
		//confidance rating:
		
        // Calculate the predicted future x, y position of the enemy
        double x = getX() + distance * Math.sin(absBearing);
        double y = getY() + distance * Math.cos(absBearing);

        // Calculate the time it takes for the bullet to travel to the predicted position
        double bulletTime = distance / (20 - 3 * bltPwr);

        // Predict the future x, y position of the enemy
        x += bulletTime * enemyVelocity * Math.sin(enemyHeading);
        y += bulletTime * enemyVelocity * Math.cos(enemyHeading);

        // Calculate the angle to the predicted future position
        double turnAngle = Math.toDegrees(Math.atan2(x - getX(), y - getY()));

        // Turn the gun to the calculated angle
        turnGunRight(normalizeRelativeAngle(turnAngle - getGunHeading()));

        // Fire at the predicted position
        fire(bltPwr);
		
		targetLocated = false;
    }

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
}
