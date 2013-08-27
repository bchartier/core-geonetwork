package org.fao.geonet.repository;

import static org.fao.geonet.repository.SpringDataTestSupport.assertSameContents;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.fao.geonet.domain.Group;
import org.fao.geonet.domain.ReservedGroup;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class GroupRepositoryTest extends AbstractSpringDataTest {

    @Autowired
    GroupRepository _repo;

    @PersistenceContext
    EntityManager _entityManager;

    private AtomicInteger _nextId = new AtomicInteger();

    @Test
    public void test_Save_Count_FindOnly_DeleteAll() throws Exception {
        assertEquals(0, _repo.count());
        Group group = newGroup();
        Group savedGroup = _repo.save(group);

        _repo.flush();
        _entityManager.clear();

        group.setId(savedGroup.getId());
        assertEquals(1, _repo.count());
        assertSameContents(group, _repo.findOne(group.getId()));
        
        _repo.deleteAll();

        _repo.flush();
        _entityManager.clear();

        assertEquals(0, _repo.count());
    }
    
    @Test
    public void testUpdate() throws Exception {
        assertEquals(0, _repo.count());
        Group group = newGroup();

        Group savedGroup = _repo.save(group);

        _repo.flush();
        _entityManager.clear();

        group.setId(savedGroup.getId());

        assertEquals(1, _repo.count());
        assertSameContents(group, _repo.findOne(group.getId()));

        group.setName("New Name");
        Group savedGroup2 = _repo.save(group);

        _repo.flush();
        _entityManager.clear();

        assertSameContents(savedGroup, savedGroup2);
        
        assertEquals(1, _repo.count());
        assertSameContents(group, _repo.findOne(group.getId()));
    }

    @Test
    public void testFindByName() throws Exception {
        Group savedGroup = _repo.save(newGroup());

        _repo.flush();
        _entityManager.clear();

        assertSameContents(savedGroup, _repo.findByName(savedGroup.getName()));
        assertNull(_repo.findByName("some wrong name"));
    }
    
    @Test
    public void testFindByEmail() throws Exception {
        Group savedGroup = _repo.save(newGroup());

        _repo.flush();
        _entityManager.clear();

        assertSameContents(savedGroup, _repo.findByEmail(savedGroup.getEmail()));
        assertNull(_repo.findByEmail("some wrong email"));
    }
    
    public void testFindReservedGroup() throws Exception {
        Group savedGroup = _repo.save(ReservedGroup.all.getGroupEntityTemplate());

        _repo.flush();
        _entityManager.clear();

        assertSameContents(savedGroup, _repo.findReservedGroup(ReservedGroup.all));
    }

    @Test
    public void testFindReservedOperation() throws Exception {
        int normalId = ReservedGroup.all.getId();
        int id = _repo.save(ReservedGroup.all.getGroupEntityTemplate()).getId();
        setId(ReservedGroup.all, id);
        try {
            _repo.save(ReservedGroup.all.getGroupEntityTemplate());

            _repo.flush();
            _entityManager.clear();

            assertSameContents(ReservedGroup.all.getGroupEntityTemplate(), _repo.findReservedGroup(ReservedGroup.all));
            assertNull(_repo.findReservedGroup(ReservedGroup.intranet));
        } finally {
            setId(ReservedGroup.all, normalId);
        }
    }
    
    @Test
    public void testFindAllIds() throws Exception {
        Group g1 = _repo.save(newGroup());
        Group g2 = _repo.save(newGroup());
        Group g3 = _repo.save(newGroup());
        
        List<Integer> ids = _repo.findIds();
        
        assertEquals(3, ids.size());
        assertEquals(g1.getId(), ids.get(0).intValue());
        assertEquals(g2.getId(), ids.get(1).intValue());
        assertEquals(g3.getId(), ids.get(2).intValue());
    }

    private void setId(ReservedGroup group, int normalId) throws Exception {
        Field declaredField = group.getClass().getDeclaredField("_id");
        declaredField.setAccessible(true);
        declaredField.set(group, normalId);
    }

    private Group newGroup() {
        int id = _nextId.incrementAndGet();
        return new Group()
                .setDescription("Desc "+id)
                .setEmail(id+"@geonet.org")
                .setName("Name "+id);
    }

}
