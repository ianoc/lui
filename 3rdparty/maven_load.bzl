

def maven_load(hash):
    native.maven_jar(
        name = hash["name"],
        artifact = hash["artifact"],
        sha1 = hash["sha1"],
        repository = _nexus,
    )
    native.bind(name = hash["bind"],
                actual = hash["actual"]
                )
