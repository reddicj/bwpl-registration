package org.bwpl.registration

class DivisionController {

    def show = {

        Division division = Division.get(params.id)
        List<Team> teams = new ArrayList<Team>(division.teams)
        teams.sort{it.name}
        [title: "$division.competition.name - $division.name",
         division: division,
         teams: teams,
         canUpdate: true]
    }
}
