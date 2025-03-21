package org.firstinspires.ftc.teamcode.systems;

import android.text.util.Linkify;

import androidx.annotation.NonNull;

import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.Action;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class SlidingArm {
	private DcMotorEx motor;

	public SlidingArm(HardwareMap hardwareMap){
		motor=hardwareMap.get(DcMotorEx.class,"slidingArm");
		motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
		motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
	}

	public class SetPosition implements Action {
		private int position;
		private boolean wait;
		private boolean initialized = false;
		public SetPosition(int position, boolean wait){
			this.position=position;
			this.wait = wait;
		}
		public SetPosition(int position){
			this(position, true);
		}
		@Override
		public boolean run(@NonNull TelemetryPacket telemetryPacket) {
			if(!this.initialized){
				if (motor.getTargetPosition() == position){
					motor.setTargetPosition(0);
				} else {
					motor.setTargetPosition(position);
				}
//				motor.setTargetPositionTolerance(15);
				motor.setMode(DcMotorEx.RunMode.RUN_TO_POSITION);
				motor.setPower(0.8);
				initialized=true;
				telemetryPacket.addLine("Sliding arm moving to position "+position);
			}
			if(this.position!=motor.getTargetPosition()){
				//if another target got set by another action, stop this action
				return false;
			}
			if(wait){
				if(motor.isBusy()){
					return true;
				}else{
					//motor.setPower(0);
					return false;
				}
			}else{
				return false;
			}
		}
	}

	public int getPosition(){
		return motor.getCurrentPosition();
	}
	public Action setPosition(int position, boolean wait){
		return new SetPosition(position, wait);
	}
	public Action setPosition(int position){
		return new SetPosition(position);
	}

}