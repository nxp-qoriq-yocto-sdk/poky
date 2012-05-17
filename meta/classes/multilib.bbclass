python multilib_virtclass_handler () {
    if not isinstance(e, bb.event.RecipePreFinalise):
        return

    cls = e.data.getVar("BBEXTENDCURR", True)
    variant = e.data.getVar("BBEXTENDVARIANT", True)
    if cls != "multilib" or not variant:
        return

    # There should only be one kernel in multilib configs
    if bb.data.inherits_class('kernel', e.data) or bb.data.inherits_class('module-base', e.data):
        raise bb.parse.SkipPackage("We shouldn't have multilib variants for the kernel")

    if bb.data.inherits_class('image', e.data):
        e.data.setVar("PN", variant + "-" + e.data.getVar("PN", False))
        return

    save_var_name=e.data.getVar("MULTILIB_SAVE_VARNAME", True) or ""
    for name in save_var_name.split():
        val=e.data.getVar(name, True)
        if val:
            e.data.setVar(name + "_MULTILIB_ORIGINAL", val)
 
    override = ":virtclass-multilib-" + variant

    e.data.setVar("MLPREFIX", variant + "-")
    e.data.setVar("PN", variant + "-" + e.data.getVar("PN", False))
    e.data.setVar("SHLIBSDIR_virtclass-multilib-" + variant ,e.data.getVar("SHLIBSDIR", False) + "/" + variant)
    if e.data.getVar("TARGET_VENDOR_virtclass-multilib-" + variant, False) is None:
	    e.data.setVar("TARGET_VENDOR_virtclass-multilib-" + variant, e.data.getVar("TARGET_VENDOR", False) + "ml" + variant)
    e.data.setVar("OVERRIDES", e.data.getVar("OVERRIDES", False) + override)
}

addhandler multilib_virtclass_handler

STAGINGCC_prepend = "${BBEXTENDVARIANT}-"

python __anonymous () {
    variant = d.getVar("BBEXTENDVARIANT", True)

    def map_dependencies(varname, d, suffix = ""):
        if suffix:
            varname = varname + "_" + suffix
        deps = d.getVar(varname, True)
        if not deps:
            return
        deps = bb.utils.explode_deps(deps)
        newdeps = []
        for dep in deps:
            if dep.endswith(("-native", "-native-runtime")):
                newdeps.append(dep)
            else:
                newdeps.append(multilib_extend_name(variant, dep))
        d.setVar(varname, " ".join(newdeps))

    if bb.data.inherits_class('image', d):
        map_dependencies("PACKAGE_INSTALL", d)
        map_dependencies("LINGUAS_INSTALL", d)
        map_dependencies("RDEPENDS", d)
        pinstall = d.getVar("LINGUAS_INSTALL", True) + " " + d.getVar("PACKAGE_INSTALL", True)
        d.setVar("PACKAGE_INSTALL", pinstall)
        d.setVar("LINGUAS_INSTALL", "")
        # FIXME, we need to map this to something, not delete it!
        d.setVar("PACKAGE_INSTALL_ATTEMPTONLY", "")
        return

    pkgs_mapping = []
    for pkg in (d.getVar("PACKAGES", True) or "").split():
        if pkg.startswith(variant):
            pkgs_mapping.append([pkg.split(variant + "-")[1], pkg])
            continue
        pkgs_mapping.append([pkg, multilib_extend_name(variant, pkg)])

    d.setVar("PACKAGES", " ".join([row[1] for row in pkgs_mapping]))

    vars = (d.getVar("PACKAGEVARS", True) or "").split()
    for pkg_mapping in pkgs_mapping:
        for subs in vars:
            d.renameVar("%s_%s" % (subs, pkg_mapping[0]), "%s_%s" % (subs, pkg_mapping[1]))

    map_dependencies("DEPENDS", d)
    for pkg in (d.getVar("PACKAGES", True).split() + [""]):
        map_dependencies("RDEPENDS", d, pkg)
        map_dependencies("RRECOMMENDS", d, pkg)
        map_dependencies("RSUGGESTS", d, pkg)
        map_dependencies("RPROVIDES", d, pkg)
        map_dependencies("RREPLACES", d, pkg)
        map_dependencies("RCONFLICTS", d, pkg)
        map_dependencies("PKG", d, pkg)

    multilib_map_variable("PROVIDES", variant, d)
    multilib_map_variable("PACKAGES_DYNAMIC", variant, d)
    multilib_map_variable("PACKAGE_INSTALL", variant, d)
    multilib_map_variable("INITSCRIPT_PACKAGES", variant, d)
}
