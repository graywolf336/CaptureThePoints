package me.dalton.capturethepoints.commands;

import java.util.ArrayList;
import java.util.List;
import me.dalton.capturethepoints.CaptureThePoints;
import me.dalton.capturethepoints.ConfigOptions;
import me.dalton.capturethepoints.beans.Team;

public class DebugCommand extends CTPCommand {
   
    /** Output states to Console. */
    public DebugCommand(CaptureThePoints instance) {
        super.ctp = instance;
        super.aliases.add("debug");
        super.notOpCommand = false;
        super.requiredPermissions = new String[]{"ctp.*", "ctp.play", "ctp.admin", "ctp.debug"};
        super.senderMustBePlayer = false;
        super.minParameters = 2;
        super.maxParameters = 3;
        super.usageTemplate = "/ctp debug [1|2]";
    }

    @Override
    public void perform() {
        int size = parameters.size();
        String pagenumber = size > 2 ? parameters.get(2) : "";
        if (pagenumber.equalsIgnoreCase("2")) {
            sendMessage("Outputting CTP info (2) to Console.");
            ctp.logInfo("-----------========== CTP DEBUG ==========-----------");
            String checkMainArena = ctp.getArenaMaster().checkArena(ctp.getArenaMaster().getSelectedArena(), player); //Check arena, if there is an error, an error message is returned.
            if (!checkMainArena.isEmpty()) {
                ctp.logInfo("Main Arena errors: " + checkMainArena);
                ctp.logInfo("-----------========== ######### ==========-----------");
                return;
            } else {
                ctp.logInfo(ctp.getArenaMaster().getSelectedArena().getName() + "'s Config Options:");
                ConfigOptions co = ctp.getArenaMaster().getSelectedArena().getConfigOptions();
                ctp.logInfo("   PointsToWin: " + co.pointsToWin);
                ctp.logInfo("   PlayTime: " + co.playTime);
                ctp.logInfo("   UseScoreGeneration: " + co.useScoreGeneration);
                ctp.logInfo("   ScoreToWin: " + co.scoreToWin);
                ctp.logInfo("   OnePointGeneratedScoreEvery30sec: " + co.onePointGeneratedScoreEvery30sec);
                ctp.logInfo("   ScoreAnnounceTime: " + co.scoreAnnounceTime);
                ctp.logInfo("   AllowBlockBreak: " + co.allowBlockBreak);
                ctp.logInfo("   AllowBlockPlacement: " + co.allowBlockPlacement);
                ctp.logInfo("   AllowCommands: " + co.allowCommands);
                ctp.logInfo("   AllowDropItems: " + co.allowDropItems);
                ctp.logInfo("   AllowLateJoin: " + co.allowLateJoin);
                ctp.logInfo("   AutoStart: " + co.autoStart);
                ctp.logInfo("   BreakingBlocksDropsItems: " + co.breakingBlocksDropsItems);
                ctp.logInfo("   DamageImmunityNearSpawnDistance: " + co.protectionDistance);
                ctp.logInfo("   DropWoolOnDeath: " + co.dropWoolOnDeath);
                ctp.logInfo("   ExactTeamMemberCount: " + co.exactTeamMemberCount);
                ctp.logInfo("   GiveNewRoleItemsOnRespawn: " + co.giveNewRoleItemsOnRespawn);
                ctp.logInfo("   GivenWoolNumber: " + co.givenWoolNumber);
                ctp.logInfo("   LobbyKickTime: " + co.lobbyKickTime);
                ctp.logInfo("   MaxPlayerHealth: " + co.maxPlayerHealth);
                ctp.logInfo("   HealthRegenFromHunger: " + co.regainHealth);
                ctp.logInfo("   MoneyAtTheLobby: " + co.moneyAtTheLobby);
                ctp.logInfo("   MoneyEvery30sec: " + co.moneyEvery30Sec);
                ctp.logInfo("   MoneyForKill: " + co.moneyForKill);
                ctp.logInfo("   MoneyForPointCapture: " + co.moneyForPointCapture);
                ctp.logInfo("   RingBlock: " + co.ringBlock);
                ctp.logInfo("   UseSelectedArenaOnly: " + co.useSelectedArenaOnly);
            }
            ctp.logInfo("-----------========== ######### ==========-----------");
            return;
        }
        
        sendMessage("Outputting CTP info (1) to Console.");
        ctp.logInfo("-----------========== CTP DEBUG ==========-----------");
        ctp.logInfo("Game running (selected arena): " + ctp.getArenaMaster().getSelectedArena().isGameRunning());
        String checkMainArena = ctp.getArenaMaster().checkArena(ctp.getArenaMaster().getSelectedArena(), player); //Check arena, if there is an error, an error message is returned.
        if (!checkMainArena.isEmpty()) {
            ctp.logInfo("Main Arena errors: "+checkMainArena);
            ctp.logInfo("-----------========== ######### ==========-----------");
            return;
        } else {
            ctp.logInfo("Main Arena is playable.");
        }
        ctp.logInfo("Running sanity checks ... ");
        
        List<String> result = new ArrayList<String>();
        if (ctp.getArenaMaster().getSelectedArena().getPlayers().size() != ctp.getArenaMaster().getSelectedArena().getPlayersData().size()
        		|| ctp.getArenaMaster().getSelectedArena().getPlayersData().size() != (ctp.getArenaMaster().getSelectedArena().getLobby().getPlayersInLobby().size() + ctp.getArenaMaster().getSelectedArena().getPlayersPlaying().size())) {
            result.add("Inconsistant number of Players: [" + ctp.getArenaMaster().getSelectedArena().getPlayersPlaying().size() + " | " + ctp.getArenaMaster().getSelectedArena().getPlayersData().size() + " | " + (ctp.getArenaMaster().getSelectedArena().getLobby().countAllPeople() + ctp.getArenaMaster().getSelectedArena().getPlayersPlaying().size()) + "]");
        }
        if (!ctp.getArenaMaster().hasSuitableArena(ctp.getArenaMaster().getSelectedArena().getPlayersPlaying().size()) && ctp.getArenaMaster().getSelectedArena().isGameRunning()) {
            result.add("No suitable arena for the number of people playing: " + ctp.getArenaMaster().getSelectedArena().getPlayersPlaying().size());
        }
        boolean error = false;
        for (String p : ctp.getArenaMaster().getSelectedArena().getPlayersData().keySet()) {
            if (p == null) {
                result.add("There is a null player in the playerData.");
                continue;
            }
            boolean isReady = false;
            if (ctp.getArenaMaster().getSelectedArena().getLobby().getPlayersInLobby().get(p) == null) {
                isReady = true;
            } else {
                isReady = ctp.getArenaMaster().getSelectedArena().getLobby().getPlayersInLobby().get(p);
            }
            if (ctp.getArenaMaster().getSelectedArena().getPlayerData(p).isReady() != isReady) {
                error = true; // Needs to be separate otherwise for loop will spam.
            }
        }
        if (error) {
            result.add("There is a discrepancy between playerData ready and the player's ready status in the lobby.");
        }
        
        for (Team aTeam : ctp.getArenaMaster().getSelectedArena().getTeams()) {
            boolean insane = aTeam.sanityCheck(ctp.getArenaMaster().getSelectedArena());
            if (insane) {
                int players = aTeam.getTeamPlayers(ctp.getArenaMaster().getSelectedArena()) == null ? 0 : aTeam.getTeamPlayers(ctp.getArenaMaster().getSelectedArena()).size(); 
                result.add("Team " + aTeam.getColor() + " has incorrect memberCount. It is different to TeamPlayers size: [" + players +" | " + aTeam.getMemberCount() + "]");
            }
        }          
        
        if (ctp.getArenaMaster().getSelectedArena().getMinPlayers() > ctp.getArenaMaster().getSelectedArena().getMaxPlayers()) {
            result.add("Minimum players greater than maximum players! [" + ctp.getArenaMaster().getSelectedArena().getMinPlayers() + " > " + ctp.getArenaMaster().getSelectedArena().getMaxPlayers() + "]");
        }
        
        if (result.isEmpty()) {
            ctp.logInfo("    Passed.");
        } else {
            for (String anError : result) {
                ctp.logInfo("    "+anError);
            }
        }
        
        result.clear();
        
        ctp.logInfo("Number of Arenas: " + ctp.getArenaMaster().getArenas().size() + ": " + ctp.getArenaMaster().getArenas());   
        ctp.logInfo("Current Arena: \""  + ctp.getArenaMaster().getSelectedArena().getName() + "\" in World \"" + ctp.getArenaMaster().getSelectedArena().getWorld() + "\"");
        if (ctp.getArenaMaster().getSelectedArena().hasLobby()) {
            ctp.logInfo("    Lobby: " + (int)ctp.getArenaMaster().getSelectedArena().getLobby().getX() + ", " + (int)ctp.getArenaMaster().getSelectedArena().getLobby().getY() + ", " + (int)ctp.getArenaMaster().getSelectedArena().getLobby().getZ() + ".");
        } else {
            ctp.logInfo("    Lobby: not made");
        }
        ctp.logInfo("    Number of capture points: " + ctp.getArenaMaster().getSelectedArena().getCapturePoints().size());
        ctp.logInfo("    Number of teams: " + ctp.getArenaMaster().getSelectedArena().getTeamSpawns().size());
        ctp.logInfo("    Minimum Players for this arena: " + ctp.getArenaMaster().getSelectedArena().getMinPlayers());
        ctp.logInfo("    Maxmimum Players for this arena: " + ctp.getArenaMaster().getSelectedArena().getMaxPlayers());
        ctp.logInfo("    Players ready in the lobby: " + ctp.getArenaMaster().getSelectedArena().getLobby().countReadyPeople() + "/" + ctp.getArenaMaster().getSelectedArena().getLobby().countAllPeople());
        ctp.logInfo(ctp.getRoles().size() + " Roles found: " + ctp.getRoles().keySet().toString());
        
        int running = 0, total = 0;
        if (ctp.getArenaMaster().getSelectedArena().getHealingItemsCooldowns() != 0) {
            running++; total++;
            result.add("Item Cooldowns");
        } else {
            total++;
        }
        if (ctp.getLobbyActivity() != 0) {
            running++; total++;
            result.add("Lobby Activity");
        } else {
            total++;
        }
        if (ctp.getArenaMaster().getSelectedArena().getMoneyScore() != 0) {
            running++; total++;
            result.add("Money Adder");
        } else {
            total++;
        }
        if (ctp.getArenaMaster().getSelectedArena().getPlayTimer() != 0) {
            running++; total++;
            result.add("Play Timer");
        } else {
            total++;
        }
        if (ctp.getArenaMaster().getSelectedArena().getPointMessenger() != 0) {
            running++; total++;
            result.add("Points Messenger");
        } else {
            total++;
        }
        
        ctp.logInfo(running+"/"+total+" Schedulers running: ");
        for (String schedule : result) {
            ctp.logInfo("    "+schedule);
        }

        ctp.logInfo("End of page 1. To view page 2 (Main Arena Config Options), type /ctp debug 2"); 
        ctp.logInfo("-----------========== ######### ==========-----------");
    }
}