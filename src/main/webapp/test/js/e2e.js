'use strict';

var appUrl = 'index.html';

function navigateTo(route) {
    browser().navigateTo(appUrl + (route ? '#'+route : ''));
}
function title() {
    return element('.breadcrumb li.active:visible').text();
}
function content() {
    return element('.content').text();
}
function url() {
    return browser().location().url();
}

describe('App', function() {
  it('should redirect to #/welcome', function() {
      navigateTo();
      expect(url()).toBe('/welcome');
  });
});

describe('Welcome view', function() {
    beforeEach(function() {
	navigateTo('/welcome');
    });

    it('should have a working navbar', function() {
	expect(title()).toBe('Accueil');
	expect(content()).toMatch('Bienvenue dans');

	expect(repeater('.sidenav li').count()).toBe(10);
	
	var btn7 = element('.sidenav li:nth-child(10) a');
	expect(btn7.text()).toMatch('A propos de');
	btn7.click();
	expect(url()).toBe('/about');
    });
});

describe('About view', function() {
    beforeEach(function() {
	navigateTo('/about');
    });
    it('should contain valid content', function () {
	expect(title()).toBe('A propos de');	  
	expect(content()).toMatch('SMS-U est un service numérique');
    });
});

describe('Groups view', function() {
    beforeEach(function() {
	navigateTo('/groups');
    });
    it('should display groups + button', function () {
	expect(title()).toBe('Groupes');	  
	expect(element("a[href='#/groups/new']").text()).toMatch('Ajouter un groupe');
	expect(element(".ngRow:first .ngCellText:first").text()).toBe('The Boss');
	expect(element(".ngRow:first .ngCellText:first a").attr('href')).toBe('#/groups/11');
    });
});

describe('Existing group view', function() {
    beforeEach(function() {
	navigateTo('/groups/11');
    });
    it('should display test group and be editable', function () {
	expect(title()).toBe('admin');	  
	expect(input('groupOrUser').val()).toBe("1");
	expect(input('wip.label').val()).toBe("The Boss");
	expect(input('group.role').val()).toBe("SUPER_ADMIN");
	expect(input('wip.supervisor').val()).toBe("");
	expect(input('group.quotaSms').val()).toBe("0");
	expect(input('group.maxPerSms').val()).toBe("1");
	expect(input('group.account').val()).toBe("0");

	expect(element(".dropdown-menu:visible").count()).toBe(0);
	input('wip.label').enter('zzzz');
	expect(element(".dropdown-menu:visible").count()).toBe(1);
	expect(element(".dropdown-menu li a").count()).toBe(5);
	element(".dropdown-menu li:nth(3) a").click();
	expect(input('wip.label').val()).toBe("User #1");

	element("form button").click();
	expect(url()).toBe('/groups');
	expect(element(".ngRow:first .ngCellText:first").text()).toBe('User #1');

	element(".ngRow:first .ngCellText:first a").click();
	expect(url()).toBe('/groups/11');
	expect(input('wip.label').val()).toBe("User #1");
	expect(title()).toBe('user1');	  
    });
});

describe('New group view', function() {
    beforeEach(function() {
	navigateTo('/groups/new');
    });
    it('should check validity of new group and create it', function () {
	expect(title()).toBe('Création');	  
	expect(input('groupOrUser').val()).toBe("0");
	expect(input('wip.label').val()).toBe("");
	expect(input('group.role').val()).toBe("?");
	expect(input('wip.supervisor').val()).toBe("");
	expect(input('group.quotaSms').val()).toBe("0");
	expect(input('group.maxPerSms').val()).toBe("");
	expect(input('group.account').val()).toBe("?");

	element("form button").click();

	var visibleErrors = "span.help-block:visible";
	//expect(element(visibleErrors).count()).toBe(2);
	expect(element(visibleErrors + ":first").text()).toBe("Required");

	expect(element(".dropdown-menu:visible").count()).toBe(0);
	input('wip.label').enter('zzzz');
	expect(element(".dropdown-menu:visible").count()).toBe(1);
	expect(element(".dropdown-menu li a").count()).toBe(3);
	element(".dropdown-menu li:nth(1) a").click();
	expect(input('wip.label').val()).toBe("All Senders");

	element("form button").click();
	expect(element(visibleErrors + ":first").text()).toBe("Already in use");

	input('wip.label').enter('zzzz');
	element(".dropdown-menu li:first a").click();
	expect(input('wip.label').val()).toBe("GroupFoo");

	element("form button").click();
	expect(element(visibleErrors + ":first").text()).toBe("Required");

	select("group.role").option("Default");

	element("form button").click();
	expect(url()).toBe('/groups');

    });
});
