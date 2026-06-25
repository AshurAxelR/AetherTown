package com.xrbpowered.aethertown.actions;

import static com.xrbpowered.aethertown.AetherTown.*;

import java.awt.event.KeyEvent;

import org.joml.Vector3f;

import com.xrbpowered.aethertown.render.tiles.TileObjectInfo;
import com.xrbpowered.aethertown.ui.dialogs.DialogContainer;
import com.xrbpowered.aethertown.world.Tile;
import com.xrbpowered.gl.ui.UINode;

public class ObserverAction extends TileAction {

	public static final TileAction sit = new ObserverAction("Sit", false);
	public static final TileAction lookout = new ObserverAction("Look outside", true);
	
	public final boolean restoreCamera;
	
	public final Vector3f savedCameraPosition = new Vector3f();
	public final Vector3f savedCameraRotation = new Vector3f();
	
	private TileAction parentAction = null;
	private Tile parentTile;
	private boolean parentAlt;
	
	private TileObjectInfo[][] points = null;
	private int pointRow, pointCol;
	
	public ObserverAction(String name, boolean restoreCamera) {
		super(name);
		this.restoreCamera = restoreCamera;
	}
	
	private boolean selectPoint(int row, int col) {
		pointRow = row;
		pointCol = col;
		TileObjectInfo info = points[pointRow][pointCol];
		if(info==null)
			return false;
		
		observer = this;
		aether.startLookController(info.position, -info.rotation);
		return true;
	}
	
	private void shiftCol(int d) {
		int cols = points[pointRow].length;
		for(int i=0; i<cols; i++) {
			if(selectPoint(pointRow, (pointCol+d+cols) % cols))
				return;
		}
	}

	private void shiftRow(int row) {
		if(points[row][pointCol]==null) {
			pointRow = row;
			shiftCol(1);
		}
		else
			selectPoint(row, pointCol);
	}

	@Override
	public boolean isEnabled(Tile tile, boolean alt) {
		return super.isEnabled(tile, alt) && (tile.t instanceof ObserverPointProvider);
	}
	
	@Override
	protected void onSuccess(Tile tile, boolean alt) {
		points = ((ObserverPointProvider) tile.t).getObserverPoints(tile, alt);
		if(points!=null) {
			UINode dialog = (UINode) ui.topChild();
			if(dialog!=null)
				DialogContainer.close(dialog);
			
			TileAction action = alt ? tile.t.getTileAltAction(tile) : tile.t.getTileAction(tile);
			parentAction = action!=this ? action : null;
			parentTile = tile;
			parentAlt = alt;
			
			savedCameraPosition.set(aether.getCamera().position);
			savedCameraRotation.set(aether.getCamera().rotation);
			selectPoint(0, 0);
		}
	}
	
	public boolean keyPressed(char c, int code) {
		switch(code) {
			case KeyEvent.VK_BACK_SPACE:
			case KeyEvent.VK_TAB:
			case KeyEvent.VK_F6:
			case KeyEvent.VK_B:
				return true;

			case KeyEvent.VK_A:
				shiftCol(-1);
				return true;
			case KeyEvent.VK_D:
				shiftCol(1);
				return true;
			case KeyEvent.VK_W:
				if(pointRow<points.length-1)
					shiftRow(pointRow+1);
				return true;
			case KeyEvent.VK_S:
				if(pointRow>0)
					shiftRow(pointRow-1);
				return true;

			case KeyEvent.VK_E:
			case KeyEvent.VK_R:
			case KeyEvent.VK_ESCAPE:
				observer = null;
				if(restoreCamera)
					aether.stopLookController(savedCameraPosition, savedCameraRotation);
				else
					aether.stopLookController(null, null);
				
				if(parentAction!=null && parentAction.isEnabled(parentTile, parentAlt))
					parentAction.performAt(parentTile, parentAlt);
				return true;

			default:
				return false;
		}
	}

}
