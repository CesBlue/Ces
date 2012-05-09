/*
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *
 * http://www.gnu.org/copyleft/gpl.html
 */
package l1j.server.server;

import static l1j.server.server.model.skill.L1SkillId.EARTH_BIND;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import l1j.server.Config;
import l1j.server.L1DatabaseFactory;
import l1j.server.server.datatables.SkillTable;
import l1j.server.server.model.L1Teleport;
import l1j.server.server.model.Instance.L1PcInstance;
import l1j.server.server.model.skill.L1SkillUse;
import l1j.server.server.serverpackets.S_SystemMessage;
import l1j.server.server.templates.L1Skill;
import l1j.server.server.utils.SQLUtil;

public class PCommands {
	private static Logger _log = Logger.getLogger(PCommands.class.getName());
	boolean spawnTF = false;
	private static PCommands _instance;

	private PCommands() {
	}

	public static PCommands getInstance() {
		if (_instance == null) {
			_instance = new PCommands();
		}
		return _instance;
	}

	public void handleCommands(L1PcInstance _player, String cmd2) {
		
		try {
			if (cmd2.equalsIgnoreCase("help")) {
				showPHelp(_player);
			} else if (cmd2.startsWith("buff")) {
				buff(_player);
			} else if (cmd2.startsWith("warp")) {
				warp(_player, cmd2);
			} else if (cmd2.startsWith("pbuff")){
				powerBuff(_player);
			} else if (cmd2.startsWith("bug")){
				reportBug(_player, cmd2);
			} else if(cmd2.startsWith("karma")){
				checkKarma(_player);
			} else if (cmd2.startsWith("drop")) {
				setDropOptions(_player, cmd2);
			}
			_log.info(_player.getName() + " used " + cmd2);
		} catch (Exception e) {
			_log.log(Level.SEVERE, e.getLocalizedMessage(), e);
		}
	}

	public void showPHelp(L1PcInstance _player){
		if (Config.PLAYER_BUFF == true && Config.PLAYER_COMMANDS == true)
			_player.sendPackets(new S_SystemMessage(
					"-warp 1-7, -karma, -buff, -bug, -drop, -help"));
		else
			_player.sendPackets(new S_SystemMessage(
					"-warp 1-7, -karma, -bug, -drop, -help"));
	}

	public void buff(L1PcInstance _player){
		int[] skillZ = {2, 3, 8, 14, 26, 42, 48, 34, 78};
		int time = 0;
		
		if (Config.PLAYER_BUFF == true && Config.PLAYER_COMMANDS == true){
			if (_player.getLevel() < 45) {
				_player.sendPackets(new S_SystemMessage("You must be lvl 45 to use this command."));
				return;
			}
			if(_player.getLevel() >= 45){
				for (int a = 0; a <= 2; a++){
					L1Skill skill = SkillTable.getInstance().findBySkillId(skillZ[a]);
					if (skill.getTarget().equals("buff")) {
					new L1SkillUse().handleCommands(_player, skillZ[a], _player.getId(), _player.getX(), _player.getY(), null, time, L1SkillUse.TYPE_SPELLSC);
					}
				}
			}
			else if (_player.getLevel() >= 50){
				for (int a = 0; a <= 5; a++){
					L1Skill skill = SkillTable.getInstance().findBySkillId(skillZ[a]);
					if (skill.getTarget().equals("buff")) {
					new L1SkillUse().handleCommands(_player, skillZ[a], _player.getId(), _player.getX(), _player.getY(), null, time, L1SkillUse.TYPE_SPELLSC);
					}
				}
			}
			else if (_player.getLevel() >= 55){
				for (int a = 0; a <= 7; a++){
					L1Skill skill = SkillTable.getInstance().findBySkillId(skillZ[a]);
					if (skill.getTarget().equals("buff")) {
					new L1SkillUse().handleCommands(_player, skillZ[a], _player.getId(), _player.getX(), _player.getY(), null, time, L1SkillUse.TYPE_SPELLSC);
					}
				}
			}
			else if (_player.getLevel() >= 60){
				for (int a = 0; a <= 8; a++){
					L1Skill skill = SkillTable.getInstance().findBySkillId(skillZ[a]);
					if (skill.getTarget().equals("buff")) {
					new L1SkillUse().handleCommands(_player, skillZ[a], _player.getId(), _player.getX(), _player.getY(), null, time, L1SkillUse.TYPE_SPELLSC);
					}
				}
			}
		} else {
			_player.sendPackets(new S_SystemMessage("Buff command is disabled."));
		}	
	}

	public void powerBuff(L1PcInstance _player){
		int[] skillZ = {2, 14, 26, 42, 43, 48, 54, 55, 68, 78, 79, 149, 151, 155, 158};
		int time = 0;
		if (Config.POWER_BUFF == true && Config.PLAYER_COMMANDS == true){
			for (int a = 0; a <= 14; a++){
				L1Skill skill = SkillTable.getInstance().findBySkillId(skillZ[a]);
				if (skill.getTarget().equals("buff")) {
				new L1SkillUse().handleCommands(_player, skillZ[a], _player.getId(), _player.getX(), _player.getY(), null, time, L1SkillUse.TYPE_SPELLSC);
				}
			}
		}else if (Config.PLAYER_COMMANDS == true && !Config.POWER_BUFF == true){
			_player.sendPackets(new S_SystemMessage("Power Buff is currently disabled!"));
		}
	}

	public void warp(L1PcInstance _player, String cmd2) {
		if(!Config.WARP) {
			_player.sendPackets(new S_SystemMessage("The -warp command is not avaliable on this server."));
			return;
		}
		
		if (!_player.getLocation().getMap().isEscapable())
		{
			_player.sendPackets(new S_SystemMessage("The -warp command is not avaliable in this area."));
			return;
		}
		
		if (_player.isPrivateShop() || _player.hasSkillEffect(EARTH_BIND) || 
				_player.isParalyzed() || _player.isPinkName() || 
				_player.isSleeped() || _player.isDead() || 
				_player.getMapId() == 99) {
			_player.sendPackets(
				new S_SystemMessage("You cannot warp in your current state."));
			return;
		}	
			
		try {
			int i = Integer.parseInt(cmd2.substring(5));
			int delaytimer = 30; 
			if (i >= 1 && i <= 7 ) {
				delaytimer *= 100;
				switch (i) {
					case 1: // Pandora
						Thread.sleep(delaytimer);
						L1Teleport.teleport(_player, 32644, 32955, (short) 0, 5, true);
						break;
					case 2: // SKT
						Thread.sleep(delaytimer);
						L1Teleport.teleport(_player, 33080, 33392, (short) 4, 5, true);
						break;
					case 3: // Giran
						Thread.sleep(delaytimer);
						L1Teleport.teleport(_player, 33442, 32797, (short) 4, 5, true);
						break;
					case 4: // Weldern
						Thread.sleep(delaytimer);
						L1Teleport.teleport(_player, 33705, 32504, (short) 4, 5, true);
						break;
					case 5: // Oren
						Thread.sleep(delaytimer);
						L1Teleport.teleport(_player, 34061, 32276, (short) 4, 5, true);
						break;
					case 6: // Orc Town
						Thread.sleep(delaytimer);
						L1Teleport.teleport(_player, 32715, 32448, (short) 4, 5, true);
						break;
					case 7: // Silent Cave
						Thread.sleep(delaytimer);
						L1Teleport.teleport(_player, 32857, 32898, (short) 304, 5, true);
				}
			} else {
				_player.sendPackets(new S_SystemMessage("Warp 1-7 only."));
			}
		} catch (Exception exception) {
			_player.sendPackets(new S_SystemMessage("-warp 1-Pandora, 2-SKT, 3-Giran, 4-Werldern, 5-Oren, 6-Orc Town, 7-Silent Cavern"));
		}
	}

	private void reportBug(L1PcInstance pc, String bug){
		Connection con = null;
		PreparedStatement pstm = null;
		
		try {
			bug = bug.substring(3).trim();
			if(bug.equals("")) {
				pc.sendPackets(new S_SystemMessage(".bug bugReport"));
				return;
			}
			con = L1DatabaseFactory.getInstance().getConnection();
			pstm = con.prepareStatement("INSERT INTO bugs (bugtext, charname, mapID, mapX, mapY, resolved) VALUES (?, ?, ?, ?, ?, 0);");
			pstm.setString(1, bug);
			pstm.setString(2, pc.getName());
			pstm.setInt(3, pc.getMapId());
			pstm.setInt(4, pc.getX());
			pstm.setInt(5, pc.getY());
			pstm.execute();
			pc.sendPackets(new S_SystemMessage("Bug reported. Thank you for your help!"));
		} catch (Exception e) {
			pc.sendPackets(new S_SystemMessage("Could not report bug: ('"+bug+"', '"+pc.getName()+"', "+pc.getMapId()+", "+pc.getX()+", "+pc.getY()+");"));
			e.printStackTrace();
		} finally {
			SQLUtil.close(pstm);
			SQLUtil.close(con);
		}
	}

	private void checkKarma(L1PcInstance pc){
		pc.sendPackets(new S_SystemMessage("Your karma is currently: " + pc.getKarma() + "."));
	
	}
	
	private void setDropOptions(final L1PcInstance pc, final String options) {
		List<String> pieces = Arrays.asList(options.split("\\s"));
		if (pieces.get(1).equals("all")) {
			if (pieces.get(2).equals("on")) {
				pc.setDropMessages(true);
				pc.setPartyDropMessages(true);
			} else {
				pc.setDropMessages(false);
				pc.setPartyDropMessages(false);
			}
		} else if (pieces.get(1).equals("party")) {
			if (pieces.get(2).equals("on"))
				pc.setPartyDropMessages(true);
			else
				pc.setPartyDropMessages(false);
		} else if (pieces.get(1).equals("mine")) {
			if (pieces.get(2).equals("on"))
				pc.setDropMessages(true);
			else
				pc.setDropMessages(false);
		} else {
			pc.sendPackets(new S_SystemMessage("-drop all|mine|party on|off"));
		}
	}
}
