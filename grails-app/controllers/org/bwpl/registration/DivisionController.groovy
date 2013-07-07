package org.bwpl.registration

class DivisionController {

    def show = {

        Division division = Division.get(params.id)
        List<Team> teams = new ArrayList<Team>(division.teams)
        teams.sort{it.name}
        List<Division> divisions = division.competition.getDivisions(true)
        divisions.addAll(division.competition.getDivisions(false))
        [title: "$division.competition.name",
         division: division,
         teams: teams,
         divisions: divisions,
         canUpdate: true]
    }
}
