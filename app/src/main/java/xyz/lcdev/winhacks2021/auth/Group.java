package xyz.lcdev.winhacks2021.auth;

import javax.security.auth.Subject;
import java.nio.file.attribute.GroupPrincipal;
import java.security.Principal;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Group implements GroupPrincipal {
    private final UUID groupid;
    private final String dispname;
    private final Set<Principal> members;
    private boolean dirty;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return groupid.equals(group.groupid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(groupid);
    }

    Group(UUID id, String name, Set<Principal> members){
        this.groupid = Objects.requireNonNull(id);
        this.dispname = Objects.requireNonNull(name);
        this.members = Objects.requireNonNull(members);
    }

    public void addMember(Principal member){
        this.members.add(member);
        this.dirty = true;
    }

    public boolean removeMember(Principal member){
        this.dirty = true;
        return this.members.remove(member);
    }

    @Override
    public String getName() {
        return groupid.toString();
    }

    @Override
    public boolean implies(Subject subject) {
        if(GroupPrincipal.super.implies(subject))
            return true;
        else
            return members.stream().anyMatch(p->p.implies(subject));
    }

    public String getDispname() {
        return dispname;
    }


}
