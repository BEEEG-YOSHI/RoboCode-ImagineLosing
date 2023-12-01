package LPrice;

import robocode.*;
import java.awt.Color;
import java.lang.Math;

public class ImagineLosing extends Robot {

    public void run() {
        setAdjustRadarForRobotTurn(true);
		double bltPwr = 1.5;
		
        while (true) {
            turnGunRight(15);
        }
    }

    public void onScannedRobot(ScannedRobotEvent e) {
        double absBearing = Math.toRadians(getHeading()) + e.getBearingRadians();
        double distance = e.getDistance();
        double enemyHeading = e.getHeadingRadians();
        double enemyVelocity = e.getVelocity();
		
		//confidance rating:::
		
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
        back(10);
    }

    public void onHitWall(HitWallEvent e) {
        
    }
}
