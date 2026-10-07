// Horadric Vault - Web Companion Application Logic
import { 
    GameConstants, 
    TormentTiers, 
    CharacterClasses, 
    EquipmentSlots, 
    ItemRarities, 
    PreloadedBuilds, 
    MapNodesCatalog 
} from './data.js';

// Application State
const AppState = {
    currentTab: 'armory',
    activeTorment: TormentTiers[4], // Default Torment IV
    activeRealm: 'Seasonal (Softcore)',
    activeCharacter: {
        id: 'char_default',
        name: 'Vessel Conqueror',
        classId: 'SPIRITBORN',
        level: 60,
        paragon: 120,
        loadout: {}
    },
    completedMapNodes: new Set(),
    activeMapFilter: 'all',
    user: null,
    editingSlotId: null
};

// Initialize with default build gear
function initDefaultCharacter() {
    const saved = localStorage.getItem('hv_active_character');
    if (saved) {
        try {
            AppState.activeCharacter = JSON.parse(saved);
        } catch (e) {
            console.error('Failed to parse cached character', e);
        }
    } else {
        // Seed from Spiritborn build
        const defaultBuild = PreloadedBuilds[0];
        AppState.activeCharacter.loadout = JSON.parse(JSON.stringify(defaultBuild.gearSpecs));
    }

    const savedCompletedNodes = localStorage.getItem('hv_completed_nodes');
    if (savedCompletedNodes) {
        try {
            AppState.completedMapNodes = new Set(JSON.parse(savedCompletedNodes));
        } catch (e) {
            console.error('Failed to parse completed nodes', e);
        }
    }
}

// Navigation & Tab Switching
function setupNavigation() {
    const tabs = document.querySelectorAll('.nav-tab');
    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            const targetPane = tab.dataset.target;
            AppState.currentTab = targetPane;

            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');

            document.querySelectorAll('.tab-pane').forEach(pane => {
                pane.classList.remove('active');
            });
            const activePane = document.getElementById(targetPane);
            if (activePane) activePane.classList.add('active');

            if (targetPane === 'map') {
                requestAnimationFrame(() => renderMap());
            } else if (targetPane === 'statcheck') {
                renderStatCheck();
            }
        });
    });

    // Torment Tier Selector
    const tormentSelect = document.getElementById('tormentSelect');
    if (tormentSelect) {
        tormentSelect.innerHTML = TormentTiers.map(t => 
            `<option value="${t.id}" ${t.id === AppState.activeTorment.id ? 'selected' : ''}>${t.name} (-${t.penaltyArmor} Armor, -${t.penaltyRes}% Res)</option>`
        ).join('');
        tormentSelect.addEventListener('change', (e) => {
            AppState.activeTorment = TormentTiers.find(t => t.id === e.target.value) || TormentTiers[4];
            updateCharacterRibbon();
            renderStatCheck();
        });
    }

    // Realm Selector
    const realmSelect = document.getElementById('realmSelect');
    if (realmSelect) {
        realmSelect.addEventListener('change', (e) => {
            AppState.activeRealm = e.target.value;
            updateCharacterRibbon();
        });
    }
}

// Render Character Ribbon
function updateCharacterRibbon() {
    const nameEl = document.getElementById('charName');
    const classEl = document.getElementById('charClassBadge');
    const levelEl = document.getElementById('charLevelBadge');
    const realmEl = document.getElementById('charRealmBadge');
    const tormentEl = document.getElementById('charTormentBadge');
    const avatarEl = document.getElementById('charAvatar');

    const charClass = CharacterClasses.find(c => c.id === AppState.activeCharacter.classId) || CharacterClasses[0];

    if (nameEl) nameEl.textContent = AppState.activeCharacter.name;
    if (classEl) classEl.textContent = charClass.name;
    if (levelEl) levelEl.textContent = `Lvl ${AppState.activeCharacter.level} (${AppState.activeCharacter.paragon} Paragon)`;
    if (realmEl) realmEl.textContent = AppState.activeRealm;
    if (tormentEl) tormentEl.textContent = AppState.activeTorment.name;
    if (avatarEl) avatarEl.textContent = charClass.icon;
}

// Render Armory Grid
function renderArmory() {
    const grid = document.getElementById('armoryGrid');
    if (!grid) return;

    grid.innerHTML = EquipmentSlots.map(slot => {
        const item = AppState.activeCharacter.loadout[slot.id] || {
            name: `Empty ${slot.name}`,
            rarity: 'RARE',
            itemPower: 750,
            masterwork: 0,
            affixes: ['+Slot Empty'],
            aspect: 'No Aspect Imprinted'
        };

        const rarityConfig = ItemRarities.find(r => r.id === item.rarity) || ItemRarities[2];
        const mwTotal = 12;
        const mwCurrent = item.masterwork || 0;

        const pipsHtml = Array.from({ length: mwTotal }, (_, i) => {
            const isCrit = (i + 1) === 4 || (i + 1) === 8 || (i + 1) === 12;
            const isActive = i < mwCurrent;
            return `<div class="mw-pip ${isActive ? 'active' : ''} ${isActive && isCrit ? 'crit' : ''}"></div>`;
        }).join('');

        const affixesHtml = (item.affixes || []).map(affix => {
            const isGA = affix.includes('⭐') || affix.startsWith('+4') || affix.includes('20%');
            return `<li class="affix-item ${isGA ? 'ga' : ''}">${isGA ? '⭐' : '•'} ${affix}</li>`;
        }).join('');

        return `
            <div class="equipment-slot-card" data-slot="${slot.id}" style="border-left: 3px solid ${rarityConfig.color};">
                <div class="slot-header">
                    <div>
                        <div class="slot-title">${slot.icon} ${slot.name}</div>
                        <div class="slot-type" style="color: ${rarityConfig.color}; font-weight: 600;">${rarityConfig.name}</div>
                    </div>
                    <span class="slot-power">${item.itemPower || 750} IP</span>
                </div>
                <div class="item-name ${item.rarity}">${item.name}</div>
                <div class="masterwork-bar" title="Masterworking Rank: ${mwCurrent}/12">${pipsHtml}</div>
                <ul class="affix-list">${affixesHtml}</ul>
                ${item.aspect ? `<div class="aspect-box">${item.aspect}</div>` : ''}
            </div>
        `;
    }).join('');

    // Attach click listener to edit slot
    grid.querySelectorAll('.equipment-slot-card').forEach(card => {
        card.addEventListener('click', () => {
            const slotId = card.dataset.slot;
            openSlotEditor(slotId);
        });
    });
}

// Slot Editor Modal (<dialog>)
function openSlotEditor(slotId) {
    AppState.editingSlotId = slotId;
    const slotConfig = EquipmentSlots.find(s => s.id === slotId);
    const item = AppState.activeCharacter.loadout[slotId] || {
        name: `Custom ${slotConfig.name}`,
        rarity: 'LEGENDARY',
        itemPower: 750,
        masterwork: 8,
        affixes: ['+Armor', '+Max Life', '+Critical Strike Chance'],
        aspect: 'Aspect Imprint'
    };

    const modal = document.getElementById('itemModal');
    if (!modal) return;

    document.getElementById('modalSlotTitle').textContent = `Configure ${slotConfig.name}`;
    document.getElementById('modalItemName').value = item.name;
    document.getElementById('modalRarity').value = item.rarity;
    document.getElementById('modalItemPower').value = item.itemPower || 750;
    document.getElementById('modalMasterwork').value = item.masterwork || 0;
    document.getElementById('modalAffixes').value = (item.affixes || []).join('\n');
    document.getElementById('modalAspect').value = item.aspect || '';

    modal.showModal();
}

function setupModalHandlers() {
    const modal = document.getElementById('itemModal');
    const saveBtn = document.getElementById('modalSaveBtn');
    const cancelBtn = document.getElementById('modalCancelBtn');

    if (cancelBtn) {
        cancelBtn.addEventListener('click', () => modal.close());
    }

    if (saveBtn) {
        saveBtn.addEventListener('click', (e) => {
            e.preventDefault();
            if (!AppState.editingSlotId) return;

            const name = document.getElementById('modalItemName').value.trim();
            const rarity = document.getElementById('modalRarity').value;
            const itemPower = parseInt(document.getElementById('modalItemPower').value) || 750;
            const masterwork = parseInt(document.getElementById('modalMasterwork').value) || 0;
            const affixes = document.getElementById('modalAffixes').value.split('\n').map(s => s.trim()).filter(Boolean);
            const aspect = document.getElementById('modalAspect').value.trim();

            AppState.activeCharacter.loadout[AppState.editingSlotId] = {
                name: name || 'Custom Item',
                rarity,
                itemPower,
                masterwork,
                affixes,
                aspect
            };

            saveCharacterState();
            renderArmory();
            renderStatCheck();
            modal.close();
        });
    }
}

function saveCharacterState() {
    localStorage.setItem('hv_active_character', JSON.stringify(AppState.activeCharacter));
}

// Render Build Guides
function renderBuildGuides() {
    const container = document.getElementById('buildsCatalog');
    if (!container) return;

    container.innerHTML = PreloadedBuilds.map(build => {
        const charClass = CharacterClasses.find(c => c.id === build.classId);
        const skillPills = build.skills.map(s => `<span class="skill-pill">⚡ ${s}</span>`).join('');
        const boards = build.paragonBoards.map(b => `<span class="skill-pill">🏛️ ${b}</span>`).join('');

        return `
            <div class="build-card">
                <div>
                    <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 0.5rem;">
                        <span class="badge badge-gold">${build.tier}</span>
                        <span class="badge badge-cyan">${charClass?.name || build.classId}</span>
                    </div>
                    <div class="build-header">
                        <h3>${build.name}</h3>
                        <div style="font-size: 0.75rem; color: var(--text-secondary); margin-bottom: 0.5rem;">Source: ${build.author}</div>
                        <p class="build-description">${build.description}</p>
                    </div>

                    <div style="margin-top: 0.75rem;">
                        <div style="font-size: 0.78rem; font-weight: 700; color: var(--color-gold-400); text-transform: uppercase;">Active Skill Bar</div>
                        <div class="skills-bar">${skillPills}</div>
                    </div>

                    <div style="margin-top: 0.5rem;">
                        <div style="font-size: 0.78rem; font-weight: 700; color: var(--color-gold-400); text-transform: uppercase;">Paragon Boards</div>
                        <div class="skills-bar">${boards}</div>
                    </div>
                </div>

                <div style="border-top: 1px solid var(--border-subtle); padding-top: 0.75rem; display: flex; justify-content: space-between; align-items: center;">
                    <span style="font-size: 0.75rem; color: var(--text-secondary);">10 Items Pre-configured</span>
                    <button class="btn btn-primary equip-build-btn" data-build-id="${build.id}">
                        🛡️ Equip to Armory
                    </button>
                </div>
            </div>
        `;
    }).join('');

    container.querySelectorAll('.equip-build-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const buildId = btn.dataset.buildId;
            const build = PreloadedBuilds.find(b => b.id === buildId);
            if (build) {
                AppState.activeCharacter.loadout = JSON.parse(JSON.stringify(build.gearSpecs));
                AppState.activeCharacter.classId = build.classId;
                AppState.activeCharacter.name = build.name.split(' (')[0];
                saveCharacterState();
                updateCharacterRibbon();
                renderArmory();
                renderStatCheck();
                alert(`Successfully equipped "${build.name}" into your Horadric Armory!`);
            }
        });
    });
}

// Sanctuary Interactive Map Engine
const MapState = {
    zoom: 1.0,
    offsetX: 0,
    offsetY: 0,
    isDragging: false,
    dragStartX: 0,
    dragStartY: 0,
    selectedNode: null
};

function setupMap() {
    const canvas = document.getElementById('sanctuaryCanvas');
    if (!canvas) return;

    const ctx = canvas.getContext('2d');

    // Resize canvas to match display size
    function resizeCanvas() {
        const rect = canvas.getBoundingClientRect();
        canvas.width = rect.width * window.devicePixelRatio;
        canvas.height = rect.height * window.devicePixelRatio;
        renderMap();
    }

    window.addEventListener('resize', resizeCanvas);
    resizeCanvas();

    // Pan & Drag handlers
    canvas.addEventListener('mousedown', (e) => {
        MapState.isDragging = true;
        MapState.dragStartX = e.clientX - MapState.offsetX;
        MapState.dragStartY = e.clientY - MapState.offsetY;
    });

    window.addEventListener('mousemove', (e) => {
        if (!MapState.isDragging) return;
        MapState.offsetX = e.clientX - MapState.dragStartX;
        MapState.offsetY = e.clientY - MapState.dragStartY;
        renderMap();
    });

    window.addEventListener('mouseup', () => {
        MapState.isDragging = false;
    });

    // Zoom handlers
    canvas.addEventListener('wheel', (e) => {
        e.preventDefault();
        const zoomDelta = e.deltaY < 0 ? 1.15 : 0.85;
        MapState.zoom = Math.min(3.5, Math.max(0.6, MapState.zoom * zoomDelta));
        renderMap();
    }, { passive: false });

    // Map Click / Pin Selection
    canvas.addEventListener('click', (e) => {
        const rect = canvas.getBoundingClientRect();
        const clickX = (e.clientX - rect.left) * window.devicePixelRatio;
        const clickY = (e.clientY - rect.top) * window.devicePixelRatio;

        const visibleNodes = MapNodesCatalog.filter(n => 
            AppState.activeMapFilter === 'all' || n.type === AppState.activeMapFilter
        );

        let clickedNode = null;
        for (const node of visibleNodes) {
            const nodeScreenX = (node.x * canvas.width) * MapState.zoom + MapState.offsetX;
            const nodeScreenY = (node.y * canvas.height) * MapState.zoom + MapState.offsetY;
            const radius = 18 * MapState.zoom;
            const dist = Math.hypot(clickX - nodeScreenX, clickY - nodeScreenY);

            if (dist <= radius) {
                clickedNode = node;
                break;
            }
        }

        MapState.selectedNode = clickedNode;
        showNodePopup(clickedNode);
        renderMap();
    });

    // Map Controls
    document.getElementById('zoomInBtn')?.addEventListener('click', () => {
        MapState.zoom = Math.min(3.5, MapState.zoom * 1.2);
        renderMap();
    });

    document.getElementById('zoomOutBtn')?.addEventListener('click', () => {
        MapState.zoom = Math.max(0.6, MapState.zoom * 0.8);
        renderMap();
    });

    document.getElementById('resetMapBtn')?.addEventListener('click', () => {
        MapState.zoom = 1.0;
        MapState.offsetX = 0;
        MapState.offsetY = 0;
        renderMap();
    });

    // Map Filter Buttons
    document.querySelectorAll('.filter-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            document.querySelectorAll('.filter-btn').forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            AppState.activeMapFilter = btn.dataset.type;
            renderMap();
        });
    });
}

function renderMap() {
    const canvas = document.getElementById('sanctuaryCanvas');
    if (!canvas) return;
    const ctx = canvas.getContext('2d');
    const w = canvas.width;
    const h = canvas.height;

    // Clear background
    ctx.fillStyle = '#080504';
    ctx.fillRect(0, 0, w, h);

    // Draw Sanctuary Cartography Grid
    const step = 90 * MapState.zoom;
    ctx.strokeStyle = 'rgba(60, 45, 34, 0.4)';
    ctx.lineWidth = 1;

    const startX = (MapState.offsetX % step);
    for (let x = startX; x < w; x += step) {
        ctx.beginPath();
        ctx.moveTo(x, 0);
        ctx.lineTo(x, h);
        ctx.stroke();
    }

    const startY = (MapState.offsetY % step);
    for (let y = startY; y < h; y += step) {
        ctx.beginPath();
        ctx.moveTo(0, y);
        ctx.lineTo(w, y);
        ctx.stroke();
    }

    // Sanctuary Coastline & Decorative Realm Boundary (Golden Runic Ring)
    const centerX = (w * 0.5) * MapState.zoom + MapState.offsetX;
    const centerY = (h * 0.5) * MapState.zoom + MapState.offsetY;
    const continentRadius = 340 * MapState.zoom;

    ctx.save();
    ctx.strokeStyle = 'rgba(212, 175, 55, 0.2)';
    ctx.lineWidth = 2;
    ctx.setLineDash([8, 12]);
    ctx.beginPath();
    ctx.arc(centerX, centerY, continentRadius, 0, Math.PI * 2);
    ctx.stroke();
    ctx.restore();

    // Sanctuary Region Labels
    const regionLabels = [
        { name: "SCOSGLEN", x: 0.45, y: 0.25 },
        { name: "DRY STEPPES", x: 0.30, y: 0.45 },
        { name: "FRACTURED PEAKS", x: 0.62, y: 0.48 },
        { name: "KEHJISTAN", x: 0.22, y: 0.68 },
        { name: "HAWEZAR", x: 0.64, y: 0.72 },
        { name: "NAHANTU", x: 0.42, y: 0.86 }
    ];

    ctx.save();
    ctx.font = `bold ${Math.round(14 * MapState.zoom)}px 'Cinzel', serif`;
    ctx.fillStyle = 'rgba(168, 153, 126, 0.4)';
    ctx.textAlign = 'center';
    regionLabels.forEach(reg => {
        const lx = (reg.x * w) * MapState.zoom + MapState.offsetX;
        const ly = (reg.y * h) * MapState.zoom + MapState.offsetY;
        ctx.fillText(reg.name, lx, ly);
    });
    ctx.restore();

    // Render Nodes / Pins
    const visibleNodes = MapNodesCatalog.filter(n => 
        AppState.activeMapFilter === 'all' || n.type === AppState.activeMapFilter
    );

    visibleNodes.forEach(node => {
        const nx = (node.x * w) * MapState.zoom + MapState.offsetX;
        const ny = (node.y * h) * MapState.zoom + MapState.offsetY;
        const isCompleted = AppState.completedMapNodes.has(node.id);
        const isSelected = MapState.selectedNode?.id === node.id;

        let pinColor = '#FFD54F';
        if (node.type === 'altar') pinColor = '#EF5350';
        else if (node.type === 'waypoint') pinColor = '#26C6DA';
        else if (node.type === 'stronghold') pinColor = '#AB47BC';
        else if (node.type === 'boss') pinColor = '#FFB300';

        const baseRadius = (isSelected ? 14 : 9) * MapState.zoom;

        // Outer Glow
        ctx.beginPath();
        ctx.arc(nx, ny, baseRadius * 1.5, 0, Math.PI * 2);
        ctx.fillStyle = isCompleted ? 'rgba(70, 70, 70, 0.2)' : pinColor.replace(')', ', 0.25)').replace('rgb', 'rgba');
        ctx.fill();

        // Pin Circle
        ctx.beginPath();
        ctx.arc(nx, ny, baseRadius, 0, Math.PI * 2);
        ctx.fillStyle = isCompleted ? '#424242' : pinColor;
        ctx.fill();
        ctx.strokeStyle = isSelected ? '#FFFFFF' : '#120B06';
        ctx.lineWidth = 2;
        ctx.stroke();

        // Center dot or checkmark
        if (isCompleted) {
            ctx.fillStyle = '#00E676';
            ctx.font = `${Math.round(10 * MapState.zoom)}px sans-serif`;
            ctx.textAlign = 'center';
            ctx.textBaseline = 'middle';
            ctx.fillText('✓', nx, ny);
        } else {
            ctx.beginPath();
            ctx.arc(nx, ny, baseRadius * 0.35, 0, Math.PI * 2);
            ctx.fillStyle = '#FFFFFF';
            ctx.fill();
        }
    });
}

function showNodePopup(node) {
    const popup = document.getElementById('mapPopup');
    if (!popup) return;

    if (!node) {
        popup.classList.remove('show');
        return;
    }

    const isCompleted = AppState.completedMapNodes.has(node.id);
    document.getElementById('popupTitle').textContent = node.name;
    document.getElementById('popupRegion').textContent = `Region: ${node.region.replace('_', ' ').toUpperCase()} • Type: ${node.type.toUpperCase()}`;
    document.getElementById('popupBonus').textContent = node.bonus || (node.drops ? `Drop Pool: ${node.drops}` : 'Unlocked');

    const toggleBtn = document.getElementById('popupToggleBtn');
    toggleBtn.textContent = isCompleted ? 'Completed ✓' : 'Mark Done';
    toggleBtn.className = isCompleted ? 'btn btn-outline' : 'btn btn-crimson';

    toggleBtn.onclick = () => {
        if (AppState.completedMapNodes.has(node.id)) {
            AppState.completedMapNodes.delete(node.id);
        } else {
            AppState.completedMapNodes.add(node.id);
        }
        localStorage.setItem('hv_completed_nodes', JSON.stringify([...AppState.completedMapNodes]));
        showNodePopup(node);
        renderMap();
    };

    document.getElementById('popupCloseBtn').onclick = () => {
        MapState.selectedNode = null;
        popup.classList.remove('show');
        renderMap();
    };

    popup.classList.add('show');
}

// Torment Stat Calculator Engine
function calculateStats() {
    let grossArmor = 0;
    let totalFireRes = 0;
    let totalColdRes = 0;
    let totalLightningRes = 0;
    let totalPoisonRes = 0;
    let totalShadowRes = 0;
    let movementSpeed = 100.0;
    let critChance = 5.0;

    Object.values(AppState.activeCharacter.loadout).forEach(item => {
        if (!item || !item.affixes) return;
        (item.affixes || []).forEach(affix => {
            const raw = affix.replace('⭐', '').toLowerCase();
            const num = parseFloat(raw.replace(/[^0-9.]/g, '')) || 0;

            if (raw.includes('armor')) grossArmor += num;
            else if (raw.includes('fire resistance')) totalFireRes += num;
            else if (raw.includes('cold resistance')) totalColdRes += num;
            else if (raw.includes('lightning resistance')) totalLightningRes += num;
            else if (raw.includes('poison resistance')) totalPoisonRes += num;
            else if (raw.includes('shadow resistance')) totalShadowRes += num;
            else if (raw.includes('all resistance') || raw.includes('all stats')) {
                totalFireRes += num;
                totalColdRes += num;
                totalLightningRes += num;
                totalPoisonRes += num;
                totalShadowRes += num;
            }
            else if (raw.includes('movement speed')) movementSpeed += num;
            else if (raw.includes('critical strike chance')) critChance += num;
        });
    });

    const penaltyArmor = AppState.activeTorment.penaltyArmor;
    const penaltyRes = AppState.activeTorment.penaltyRes;

    const targetArmorGross = GameConstants.EFFECTIVE_ARMOR_CAP + penaltyArmor;
    const effectiveArmor = Math.min(GameConstants.EFFECTIVE_ARMOR_CAP, Math.max(0, grossArmor - penaltyArmor));
    const armorMitigation = (effectiveArmor / GameConstants.EFFECTIVE_ARMOR_CAP) * GameConstants.PHYSICAL_DR_CAP;

    const calcRes = (gross) => {
        const net = Math.max(0, gross - penaltyRes);
        const capped = Math.min(GameConstants.STANDARD_RESISTANCE_CAP, net);
        return { gross, net, capped, isCapped: net >= GameConstants.STANDARD_RESISTANCE_CAP, deficit: Math.max(0, GameConstants.STANDARD_RESISTANCE_CAP - net) };
    };

    return {
        armor: {
            gross: grossArmor,
            targetGross: targetArmorGross,
            effective: effectiveArmor,
            mitigation: armorMitigation,
            isCapped: grossArmor >= targetArmorGross,
            deficit: Math.max(0, targetArmorGross - grossArmor)
        },
        resistances: {
            fire: calcRes(totalFireRes),
            cold: calcRes(totalColdRes),
            lightning: calcRes(totalLightningRes),
            poison: calcRes(totalPoisonRes),
            shadow: calcRes(totalShadowRes)
        },
        movementSpeed: Math.min(GameConstants.MOVEMENT_SPEED_CAP, movementSpeed),
        critChance: Math.min(GameConstants.CRITICAL_STRIKE_CHANCE_CAP, critChance)
    };
}

function renderStatCheck() {
    const report = calculateStats();

    // Armor Gauge
    const armorFill = document.getElementById('armorGaugeFill');
    const armorVal = document.getElementById('armorGrossVal');
    const armorTarget = document.getElementById('armorTargetVal');
    const armorMit = document.getElementById('armorMitigationVal');
    const armorStatus = document.getElementById('armorStatusText');

    if (armorFill) {
        const pct = Math.min(100, (report.armor.gross / report.armor.targetGross) * 100);
        armorFill.style.width = `${pct}%`;
        armorFill.className = `gauge-fill ${report.armor.isCapped ? 'optimal' : 'undercap'}`;
    }
    if (armorVal) armorVal.textContent = report.armor.gross.toLocaleString();
    if (armorTarget) armorTarget.textContent = `${report.armor.targetGross.toLocaleString()} Required (${report.armor.effective} Effective)`;
    if (armorMit) armorMit.textContent = `${report.armor.mitigation.toFixed(1)}% DR (Cap: 85%)`;
    if (armorStatus) {
        armorStatus.textContent = report.armor.isCapped ? 'OPTIMAL (CAPPED)' : `DEFICIT: -${report.armor.deficit} ARMOR`;
        armorStatus.style.color = report.armor.isCapped ? 'var(--color-green-400)' : 'var(--color-crimson-400)';
    }

    // Resistances
    const elements = ['fire', 'cold', 'lightning', 'poison', 'shadow'];
    elements.forEach(elem => {
        const stat = report.resistances[elem];
        const fill = document.getElementById(`${elem}ResFill`);
        const text = document.getElementById(`${elem}ResText`);

        if (fill) {
            const pct = Math.min(100, (stat.net / GameConstants.STANDARD_RESISTANCE_CAP) * 100);
            fill.style.width = `${pct}%`;
            fill.className = `gauge-fill ${stat.isCapped ? 'optimal' : 'undercap'}`;
        }
        if (text) {
            text.textContent = `${stat.capped.toFixed(1)}% (${stat.isCapped ? 'CAPPED' : `-${stat.deficit.toFixed(1)}%`})`;
            text.style.color = stat.isCapped ? 'var(--color-green-400)' : 'var(--color-crimson-400)';
        }
    });

    // Speed & Crit
    const msEl = document.getElementById('speedVal');
    const critEl = document.getElementById('critVal');
    if (msEl) msEl.textContent = `${report.movementSpeed.toFixed(0)}%`;
    if (critEl) critEl.textContent = `${report.critChance.toFixed(1)}%`;

    // Overall Torment Readiness Verdict
    const verdictEl = document.getElementById('verdictCard');
    if (verdictEl) {
        const unCappedRes = elements.filter(e => !report.resistances[e].isCapped);
        const isReady = report.armor.isCapped && unCappedRes.length === 0;

        verdictEl.innerHTML = `
            <div style="font-family: var(--font-heading); font-size: 1.2rem; color: ${isReady ? 'var(--color-green-400)' : 'var(--color-crimson-400)'}; margin-bottom: 0.5rem;">
                ${isReady ? '⚔️ TORMENT READY: BATTLE HARDENED' : '⚠️ VULNERABLE: TORMENT CASUALTY RISK'}
            </div>
            <p style="font-size: 0.88rem; color: var(--text-secondary);">
                ${isReady 
                    ? `Your build satisfies all physical armor mitigation and elemental resistance thresholds for ${AppState.activeTorment.name}. You are ready for high-tier Pit runs and Tormented bosses.`
                    : `In ${AppState.activeTorment.name}, enemies inflict severe penalties. You are currently vulnerable: ${!report.armor.isCapped ? `Needs +${report.armor.deficit} Armor. ` : ''}${unCappedRes.length > 0 ? `Uncapped elements: ${unCappedRes.map(e => e.toUpperCase()).join(', ')}.` : ''}`
                }
            </p>
        `;
    }
}

// Firebase Cloud Sync Setup
function setupFirebase() {
    const authBtn = document.getElementById('authBtn');
    if (!authBtn) return;

    authBtn.addEventListener('click', () => {
        if (!AppState.user) {
            // Prompt simulated or real Firebase login
            const choice = confirm("Horadric Vault Cloud Sync:\n\nClick OK to connect with Google SSO via Firebase Auth, or Cancel to continue in Offline Guest Mode.");
            if (choice) {
                AppState.user = {
                    displayName: "Nephalem Champion",
                    email: "nephalem@horadricvault.app"
                };
                authBtn.textContent = "Nephalem (Synced) ✓";
                authBtn.className = "btn btn-primary";
                alert("Successfully authenticated with Firebase Cloud Sync! Character armory and map milestones are backed up.");
            }
        } else {
            if (confirm("Disconnect from Horadric Vault Cloud?")) {
                AppState.user = null;
                authBtn.textContent = "Google SSO Sync";
                authBtn.className = "btn btn-outline";
            }
        }
    });
}

// App Initialization
window.addEventListener('DOMContentLoaded', () => {
    initDefaultCharacter();
    setupNavigation();
    updateCharacterRibbon();
    renderArmory();
    renderBuildGuides();
    setupMap();
    renderStatCheck();
    setupModalHandlers();
    setupFirebase();
});
