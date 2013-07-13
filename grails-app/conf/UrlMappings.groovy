class UrlMappings {

	static mappings = {

        "/"(controller: "competition", action: "list")
        "/competition/$action/$id?"(controller: "competition")
        name competitionMapping: "/$competition/$controller/$action?/$id?"()

		"500"(view:'/error')
	}
}
