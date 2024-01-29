import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.UserManager;
/**
 * Script to transfer members from one group to another.
 */
boolean dryRun = true; // Set this to true for a dry run
UserManager userManager = resourceResolver.adaptTo(UserManager.class);

def fromGroup = "eaton-dam-admin" 
def toGroup = "eaton-dam-users"

Group sourceGroup = (Group) userManager.getAuthorizable(fromGroup);
Group targetGroup = (Group) userManager.getAuthorizable(toGroup);

if (sourceGroup != null && targetGroup != null) {
    Iterator<Authorizable> members = sourceGroup.getMembers();
    while (members.hasNext()) {
        Authorizable member = members.next();
        
        if (dryRun) {
            println("User would be transferred during dry run: " + member.getPrincipal().getName());
        } else {
            boolean addedToTarget = targetGroup.addMember(member);
            
            if (addedToTarget) {
                println("User transferred: " + member.getPrincipal().getName());
            } else {
                println("Failed to transfer user: " + member.getPrincipal().getName());
            }
        }
    }
    
    if (!dryRun) {
        save();
        println("Current session is saved.");
    } else {
        println("Dry run: User transfer simulation.");
    }
} else {
    println("Source group or target group not found.");
}
