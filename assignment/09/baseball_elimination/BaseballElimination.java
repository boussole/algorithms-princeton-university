import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class BaseballElimination {
    private class Team {
        private String name;
        private int wins;
        private int losses;
        private int remaining;
        private int[] against;
        private boolean isEliminated;
        private int battles;
        private LinkedList<String> certificateOfElimination = new LinkedList<String>();
    }
    
    private int numberOfTeams;
    private int numberOfGames;
    /* a battle means the series of games between two teams */
    private int numberOfBattles;
    private Team[] teams;
    private Map<String, Integer> teamsMap = new HashMap<>();

    /**
     * create a baseball division from given filename in format specified below
     * @param filename
     */
    public BaseballElimination(String filename) {
        In teamsIn = new In(filename);
        
        numberOfTeams = Integer.parseInt(teamsIn.readLine());
        teams = new Team[numberOfTeams];
        
        for (int i = 0; i < numberOfTeams; ++i) {
            String line = teamsIn.readLine();
            String[] lex = line.trim().split("\\s+");

            Team team = new Team();
            team.name = lex[0];
            team.wins = Integer.parseInt(lex[1]);
            team.losses = Integer.parseInt(lex[2]);
            team.remaining = Integer.parseInt(lex[3]);
            team.against = new int[numberOfTeams];
            for (int j = 0; j < numberOfTeams; ++j) {
                team.against[j] = Integer.parseInt(lex[4 + j]);
                if (team.against[j] > 0) {
                    ++team.battles;
                    if (j > i) {
                        ++numberOfBattles;
                    }
                }
            }
            
            teamsMap.put(lex[0], i);
            teams[i] = team;
            numberOfGames += team.remaining;
        }
        
        numberOfGames = numberOfGames / 2;
    }

    /**
     * number of teams
     * @return
     */
    public int numberOfTeams() {
        return numberOfTeams;
    }
    
    /**
     * all teams
     * @return
     */
    public Iterable<String> teams() {
        return teamsMap.keySet();
    }

    private int getTeamIdChecked(String team) {
        Integer id = teamsMap.get(team);
        if (id == null) {
            throw new java.lang.IllegalArgumentException();
        }
        return id;
    }
    
    /**
     * number of wins for given team
     * @param team
     * @return
     */
    public int wins(String team) {
        return teams[getTeamIdChecked(team)].wins;
    }

    /**
     * number of losses for given team
     * @param team
     * @return
     */
    public int losses(String team) {
        return teams[getTeamIdChecked(team)].losses;
    }

    /**
     * number of remaining games for given team
     * @param team
     * @return
     */
    public int remaining(String team) {
        return teams[getTeamIdChecked(team)].remaining;
    }

    /**
     * number of remaining games between team1 and team2
     * @param team1
     * @param team2
     * @return
     */
    public int against(String team1, String team2) {
        // small optimization
        if (team1.equals(team2)) {
            return 0;
        }
        
        return teams[getTeamIdChecked(team1)].against[getTeamIdChecked(team2)];
    }

    /**
     * is given team eliminated?
     * @param team
     * @return
     */
    public boolean isEliminated(String team) {
        int id = getTeamIdChecked(team);
        if (teams[id].isEliminated) {
            return true;
        }
        
        int numberOfTeamVertex = numberOfTeams; /* don't skip researched team to keep [0..nubmerOfTeams] indices */
        int numberOfBattleVertex = numberOfBattles - teams[id].battles;
        int s = numberOfTeamVertex + numberOfBattleVertex;
        int t = s + 1;
        FlowNetwork flowNetwork = new FlowNetwork(t + 1);
        /* assuming `team` won all it remaining games */
        int maxWins = teams[id].wins + teams[id].remaining;
        // 1. build graph
        for (int teamVertex = 0, battleVertex = numberOfTeamVertex; teamVertex < numberOfTeamVertex; ++teamVertex) {
            if (teamVertex == id) { 
                continue; 
            }

            int wins = maxWins - teams[teamVertex].wins;
            if (wins < 0) {
                teams[id].isEliminated = true;
                teams[id].certificateOfElimination.add(teams[teamVertex].name);
            }

            if (teams[id].isEliminated) {
                continue;
            }

            FlowEdge teamToDstEdge = new FlowEdge(teamVertex, t, wins);
            flowNetwork.addEdge(teamToDstEdge);
            for (int j = teamVertex + 1; j < teams[teamVertex].against.length; ++j) {
                if (j != id && teams[teamVertex].against[j] > 0) {
                    FlowEdge sourceToBattleEdge = new FlowEdge(s, battleVertex, teams[teamVertex].against[j]);
                    flowNetwork.addEdge(sourceToBattleEdge);
                    
                    FlowEdge battleToTeam1Edge = new FlowEdge(battleVertex, teamVertex, Integer.MAX_VALUE);
                    flowNetwork.addEdge(battleToTeam1Edge);
                    
                    FlowEdge battleToTeam2Edge = new FlowEdge(battleVertex, j, Integer.MAX_VALUE);
                    flowNetwork.addEdge(battleToTeam2Edge);
                    
                    battleVertex++;
                }
            }
        }

        // 2. Checking trivial case
        if (teams[id].isEliminated) {
            return true;
        }

        // 3. Calculate MaxFlow
        FordFulkerson fordFulkerson = new FordFulkerson(flowNetwork, s, t);
        
        // 4. Check elimination and make certificate
        for (FlowEdge source : flowNetwork.adj(s)) {
            if (source.residualCapacityTo(source.to()) > 0) {
                teams[id].isEliminated = true;
                break;
            }
        }

        // 5. Make certificate
        if (teams[id].isEliminated) {
            for (FlowEdge dst : flowNetwork.adj(t)) {
                if (fordFulkerson.inCut(dst.from())) {
                    teams[id].certificateOfElimination.addFirst(teams[dst.from()].name);
                }
            }
        }

        return teams[id].isEliminated;
    }

    /**
     * subset R of teams that eliminates given team; null if not eliminated
     * @param team
     * @return
     */
    public Iterable<String> certificateOfElimination(String team) {
        if (isEliminated(team)) {
            return teams[getTeamIdChecked(team)].certificateOfElimination;
        }

        return null;
    }
    
    private static void checkElimintaion(BaseballElimination division, String team) {
        if (division.isEliminated(team)) {
            StdOut.print(team + " is eliminated by the subset R = { ");
            for (String t : division.certificateOfElimination(team)) {
                StdOut.print(t + " ");
            }
            StdOut.println("}");
        }
        else {
            StdOut.println(team + " is not eliminated");
        }
    }
    
    public static void main(String[] args) {
        BaseballElimination division = new BaseballElimination(args[0]);
        
        // numberOfTeams
        System.out.printf("numberOfTeams = %d\n", division.numberOfTeams);
        // -------------

        // teams info 
        for (String team : division.teams()) {
            System.out.printf("name = '%s'\t|\t", team);
            System.out.printf("wins = %d\t|\t", division.wins(team));
            System.out.printf("losses = %d\t|\t", division.losses(team));
            System.out.printf("remaining = %d\n", division.remaining(team));
        }
        // -------------

        // against
        for (String team1 : division.teams()) {
            for (String team2 : division.teams()) {
                System.out.printf("'%s' vs '%s' = %d\n", team1, team2, division.against(team1, team2));
            }
        }
        //--------------

        if (args.length > 1) {
            checkElimintaion(division, args[1]);
        }
        else {
            for (String team : division.teams()) {
                checkElimintaion(division, team);
            }
        }
    }
};
