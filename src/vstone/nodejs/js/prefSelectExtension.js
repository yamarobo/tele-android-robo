// --------------------------------------------------------------------
// Author  : mashimonator
// Create  : 2009/10/07
// Update  : 2009/10/07
// Description : 驛ｽ驕灘?���?�?驕ｸ謚槭?��励Ν繝�繧?��繝ｳ繧�?��?��縺?��譏薙?��縺吶?�?
// --------------------------------------------------------------------

/*@cc_on 
var doc = document;
eval('var document = doc');
@*/
var prefSelectExtension = {
	//-----------------------------------------
	// 險?��螳壼?��?��
	//-----------------------------------------
	conf : {
		targetClass : 'pref',
		selectSerial : 'prefSelectExtension_###_target',
		shimSerial : 'prefSelectExtension_###_shim',
		idBaseStr : 'prefSelectExtension',
		viewingSerial : null,
		viewingMenu : null,
		viewingShim : null,
		selectedPref : null,
		count : 0
	},
	//-----------------------------------------
	// 蝨?���?��繝ｻ驛ｽ驕灘?���?�?��?��?��select隕∫?��?��縺?��optgroup隕∫?��?��縺悟�?�縺?��繧後�?�縺?��蝣?��蜷医↓菴?��逕ｨ縺吶?���?��?��
	//-----------------------------------------
	pref : [
		[ '蛹玲?��?��驕�', [ '蛹玲?��?��驕�' ] ],
		[ '譚ｱ蛹?��', [ '髱呈｣?��逵?��', '遘狗伐�?�?��', '蟯?���?狗恁', '螻?��蠖｢逵?��', '螳?��蝓守恁', '遖丞ｳ?��逵?��' ] ],
		[ '髢?��譚ｱ', [ '譬?��譛ｨ逵?��', '鄒､鬥?��逵?��', '闌ｨ蝓守恁', '蝓ｼ�?臥�?', '蜊�闡臥�?', '譚ｱ�??��驛ｽ', '逾槫?��亥?��晉恁' ] ],
		[ '荳?��驛ｨ', [ '螻?��譴?��逵?��', '髟ｷ驥守恁', '�?��貎溽�?', '�?悟ｱ?��逵?��', '遏ｳ蟾晉恁', '遖丈ｺ慕恁', '髱吝ｲ?��逵?��', '蟯宣?���?�?', '諢帷衍�?�?��' ] ],
		[ '霑�?�柄', [ '荳蛾㍾逵?��', '蜥梧?��悟ｱ?��逵?��', '螂郁憶逵?��', '貊玖?��?��逵?��', '�??��驛ｽ蠎�', '螟ｧ髦?��蠎�', '蜈ｵ蠎ｫ逵?��' ] ],
		[ '荳?��蝗ｽ', [ '魑･蜿也恁', '蟯?��螻?��逵?��', '蟲?��譬?��逵?��', '蠎�蟲?��逵?��', '螻?��蜿?��逵?��' ] ],
		[ '蝗帛�?', [ '蠕ｳ蟲?��逵?��', '鬥吝ｷ晉恁', '諢帛ｪ帷�?', '鬮倡衍�?�?��' ] ],
		[ '荵晏ｷ?��', [ '遖丞ｲ?��逵?��', '菴�??��?��逵?��', '髟ｷ�?守恁', '辭頑悽逵?��', '螟ｧ蛻?��逵?��', '螳?���?守恁', '鮖ｿ蜈仙ｳ?��逵?��', '豐也ｸ?��逵?��' ] ]
	],
	//-----------------------------------------
	// 蛻�?�?�?���??��
	//-----------------------------------------
	init : function() {
		// �?��雎｡縺?��select隕∫?��?��繧貞叙蠕�
		var selects = prefSelectExtension.getTargetElements('select', prefSelectExtension.conf.targetClass);
		var len = selects.length;
		if ( prefSelectExtension.isUnderIE7() ) {
			// ---IE7莉･荳九�?��蝣?��蜷?��
			for ( var i = 0; i < len; i++ ) {
				// select隕∫?��?��縺?��繧?��繝ｪ繧?��繝ｫ繧�?��伜�??
				selects[i].className += ' ' + prefSelectExtension.getSelectSerial();
				// 繧?��繝吶Φ繝医↓髢?��謨?��繧�?��伜�??
				selects[i].onmousedown = function() {
					var str = this.className.split('_');
					prefSelectExtension.createPulldown(str[1]);
				}
				// 繧?��繧?��繝ｳ繝医�?繝�繝�
				prefSelectExtension.conf.count += 1;
			}
		} else {
			// ---IE8,Firefox,Safarai,Opera,Chrome
			for ( var i = 0; i < len; i++ ) {
				// select隕∫?��?��縺?��繧?��繝ｪ繧?��繝ｫ繧�?��伜�??
				selects[i].className += ' ' + prefSelectExtension.getSelectSerial();
				// select隕∫?��?��縺?��諡?��蠑ｵ繧呈命縺?��
				prefSelectExtension.setExtension( selects[i] );
				// 繧?��繧?��繝ｳ繝医�?繝�繝�
				prefSelectExtension.conf.count += 1;
			}
		}
		// 逍台?��?��繝励Ν繝�繧?��繝ｳ縺?��蜑企勁�?���??��繧�?��伜�??
		prefSelectExtension.addEvent( document, 'click', prefSelectExtension.removePulldown );
	},
	//-----------------------------------------
	// select隕∫?��?��縺?��諡?��蠑ｵ�?���??��
	//-----------------------------------------
	setExtension : function( element ) {
		// select隕∫?��?��縺?��繧?��繧?��繧?��繧貞叙蠕�
		var size = prefSelectExtension.getElementSize(element);
		// select隕∫?��?��縺?��繝昴ず繧?��繝ｧ繝ｳ繧貞叙蠕�
		var pos = prefSelectExtension.getElementPosition(element);
		// select隕∫?��?��逕ｨ縺?��shim繧堤函謌�
		prefSelectExtension.createShim( pos.top, pos.left, size.width, size.height, false );
		// select隕∫?��?��縺?��陲?��縺帙ｋlayer繧堤函謌�
		prefSelectExtension.createLayer( pos.top, pos.left, size.width, size.height );
	},
	//-----------------------------------------
	// 逍台?��?��繝励Ν繝�繧?��繝ｳ繧定｡?��遉ｺ縺吶?�?
	//-----------------------------------------
	createPulldown : function( serial ) {
		// 譌｢縺?��陦?��遉ｺ荳?��縺?��menu縺後≠繧後�?��蜑企勁縺吶?�?
		if ( prefSelectExtension.removePulldownInCreate(serial) ) {
			// 繧?��繝ｪ繝�繧?��縺輔ｌ縺溯?��∫?��?��縺後�励Ν繝�繧?��繝ｳ陦?��遉ｺ荳?��縺?��select隕∫?��?��縺?��縺�??��縺?��謚懊?�?繧?��
			return;
		}
		// 繧?��繝ｪ繝�繧?��縺輔ｌ縺殱elect隕∫?��?��繧貞叙蠕�
		var targetSerial = prefSelectExtension.conf.selectSerial.replace( '###', serial );
		var select = prefSelectExtension.getTargetElements( 'select', targetSerial );
		if ( prefSelectExtension.isUnderIE7() ) {
			select[0].disabled = true;
		}
		if ( select[0] ) {
			// div隕∫?��?��繧堤函謌�
			var div = document.createElement('div');
			div.id = prefSelectExtension.conf.idBaseStr + '_menu';
			div.style.zIndex = '150';
			// dl隕∫?��?��繧堤函謌�
			var dl = document.createElement('dl');
			dl.className = prefSelectExtension.conf.idBaseStr;
			// ---select隕∫?��?��縺?��optgroup隕∫?��?��縺悟�?�縺?��繧九°蜷?��縺九�?�蛻?��蟯?��
			if ( !prefSelectExtension.existOptgroup(select[0]) ) {
				// selected螻樊�?��縺梧�?螳壹?�?繧後�?�縺?��繧狗恁繧貞叙蠕�
				prefSelectExtension.conf.selectedPref = prefSelectExtension.getSelectedOption(select[0]);
				var areaLen = prefSelectExtension.pref.length;
				for ( var i = 0; i < areaLen; i++ ) {
					// dt隕∫?��?��繧堤函謌�
					var dt = document.createElement('dt');
					dt.className = prefSelectExtension.conf.idBaseStr + '_areaLabel';
					dt.innerHTML = prefSelectExtension.pref[i][0];
					// dt隕∫?��?��繧壇l隕∫?��?��縺?��霑ｽ蜉�
					dl.appendChild(dt);
					// dd隕∫?��?��繧堤函謌�
					var dd = document.createElement('dd');
					dd.className = prefSelectExtension.conf.idBaseStr + '_area';
					// 蝨?��蝓溘�?�螻槭�?繧狗恁縺?��謨?��縺?��縺銑OOP
					var prefLen = prefSelectExtension.pref[i][1].length;
					for ( var x = 0; x < prefLen; x++ ) {
						if ( prefSelectExtension.conf.selectedPref && prefSelectExtension.pref[i][1][x].match(prefSelectExtension.conf.selectedPref) ) {
							// span隕∫?��?��繧堤函謌�
							var span = document.createElement('span');
							span.className = prefSelectExtension.conf.idBaseStr + '_selected';
							span.innerHTML = prefSelectExtension.pref[i][1][x];
							// span隕∫?��?��繧壇d隕∫?��?��縺?��霑ｽ蜉�
							dd.appendChild(span);
						} else {
							// a隕∫?��?��繧堤函謌�
							var a = document.createElement('a');
							a.className = prefSelectExtension.conf.idBaseStr + '_link';
							a.innerHTML = prefSelectExtension.pref[i][1][x];
							// href螻樊�?��繧偵そ繝�繝�
							a.setAttribute('href',"javascript:prefSelectExtension.setSelectValue('" + prefSelectExtension.getOptionValueFromPrefName(select[0],prefSelectExtension.pref[i][1][x]) + "');");
							// a隕∫?��?��繧壇d隕∫?��?��縺?��霑ｽ蜉�
							dd.appendChild(a);
						}
					}
					// dd隕∫?��?��繧壇l隕∫?��?��縺?��霑ｽ蜉�
					dl.appendChild(dd);
				}
			} else {
				// select隕∫?��?��縺?���?�??��∫?��?��繧貞叙蠕�
				var children = select[0].childNodes;
				var len = children.length;
				for (var i = 0; i < len; i++) {
					// �?�??��∫?��?��縺系ode縺?��蝣?��蜷医?��?��縺?���?���??��
					if ( children[i].nodeType == '1' ) {
						if ( children[i].nodeName.match(/optgroup/i) ) {
							//--- optgroup隕∫?��?��縺?��蝣?��蜷?��
							// dt隕∫?��?��繧堤函謌�
							var dt = document.createElement('dt');
							dt.className = prefSelectExtension.conf.idBaseStr + '_areaLabel';
							dt.innerHTML = children[i].getAttribute('label');
							// dt隕∫?��?��繧壇l隕∫?��?��縺?��霑ｽ蜉�
							dl.appendChild(dt);
							// optgroup隕∫?��?��縺?���?�??��∫?��?��繧貞叙蠕�
							var grandchildren = children[i].childNodes;
							var len2 = grandchildren.length;
							// dd隕∫?��?��繧堤函謌�
							var dd = document.createElement('dd');
							dd.className = prefSelectExtension.conf.idBaseStr + '_area';
							for (var x = 0; x < len2; x++) {
								if ( grandchildren[x].nodeType == '1' && grandchildren[x].nodeName.match(/option/i) ) {
									if ( !grandchildren[x].selected ) {
										// a隕∫?��?��繧堤函謌�
										var a = document.createElement('a');
										a.className = prefSelectExtension.conf.idBaseStr + '_link';
										a.innerHTML = grandchildren[x].innerHTML;
										// href螻樊�?��繧偵そ繝�繝�
										a.setAttribute('href',"javascript:prefSelectExtension.setSelectValue('" + grandchildren[x].value + "');");
										// a隕∫?��?��繧壇d隕∫?��?��縺?��霑ｽ蜉�
										dd.appendChild(a);
									} else {
										// span隕∫?��?��繧堤函謌�
										var span = document.createElement('span');
										span.className = prefSelectExtension.conf.idBaseStr + '_selected';
										span.innerHTML = grandchildren[x].innerHTML;
										// span隕∫?��?��繧壇d隕∫?��?��縺?��霑ｽ蜉�
										dd.appendChild(span);
									}
								}
							}
							// dd隕∫?��?��繧壇l隕∫?��?��縺?��霑ｽ蜉�
							dl.appendChild(dd);
						}
					}
				}
			}
			// dl隕∫?��?��繧壇iv隕∫?��?��縺?��霑ｽ蜉�
			div.appendChild(dl);
			div.style.position = 'absolute';
			div.style.visibility = 'hidden';
			div.style.top = '0px';
			div.style.left = '0px';
			// html縺?��蜿肴�?
			document.body.appendChild(div);
			// select隕∫?��?��縺?��繝昴ず繧?��繝ｧ繝ｳ繝ｻ繧?��繧?��繧?��繧貞叙蠕�
			var pos = prefSelectExtension.getElementPosition( select[0] );
			var size = prefSelectExtension.getElementSize( select[0] );
			var divSize = prefSelectExtension.getElementSize( div );
			// 繝悶Λ繧?��繧?��縺?��逕ｻ髱?��繧?��繧?��繧?��繧貞叙蠕�
			var browserSize = prefSelectExtension.getBrowserSize();
			var tmpTop = 0;
			var tmpLeft = 0;
			// 繝励Ν繝�繧?��繝ｳ陦?��遉ｺ菴咲?��?��?��?��域ｨ?��?��?��芽?��?��螳?��
			if ( (pos.left + size.width + divSize.width) <= browserSize.width ) {
				// 蜿弱∪繧句?��?��蜷?��
				tmpLeft = pos.left;
			} else {
				// 縺?��縺?���?��繧句?��?��蜷?��
				tmpLeft = (pos.left - (divSize.width - size.width));
			}
			// 繝励Ν繝�繧?��繝ｳ陦?��遉ｺ菴咲?��?��?��?��育?��?��?��?��芽?��?��螳?��
			if ( (pos.top + size.height + divSize.height) <= browserSize.height ) {
				// 蜿弱∪繧句?��?��蜷?��
				tmpTop = (pos.top + size.height + 1);
			} else {
				// 縺?��縺?���?��繧句?��?��蜷?��
				tmpTop = (pos.top - (divSize.height + 1));
			}
			// 鬮倥?�?縺?��蟷?��繧偵そ繝�繝�
			div.style.left = tmpLeft + 'px';
			div.style.top = tmpTop + 'px';
			// 謫?��莨?��繝励Ν繝�繧?��繝ｳ繧定｡?��遉ｺ
			div.style.visibility = 'visible';
			// 陦?��遉ｺ荳?��縺?��逍台?��?��繝励Ν繝�繧?��繝ｳ繧�?���?�?
			prefSelectExtension.conf.viewingMenu = div;
			// 逍台?��?��繝励Ν繝�繧?��繝ｳ繧定｡?��遉ｺ荳?��縺?��select隕∫?��?��縺?��繧?��繝ｪ繧?��繝ｫ繧�?���?�?
			prefSelectExtension.conf.viewingSerial = serial;
			// shim繧堤函謌�
			prefSelectExtension.createShim( tmpTop, tmpLeft, div.scrollWidth, div.scrollHeight, true );
			// focus繧堤?��?��蜍�
			div.focus();
		}
	},
	//-----------------------------------------
	// 逍台?��?��繝励Ν繝�繧?��繝ｳ縺?��驕ｸ謚槭?�?繧後◆蛟､繧痴elect隕∫?��?��縺?��繧?��繝�繝医�?繧?��
	//-----------------------------------------
	setSelectValue : function( index ) {
		// �?��雎｡縺?��select隕∫?��?��繧貞叙蠕�
		var targetSerial = prefSelectExtension.conf.selectSerial.replace( '###', prefSelectExtension.conf.viewingSerial );
		var select = prefSelectExtension.getTargetElements( 'select', targetSerial );
		// 謫?��莨?��繝励Ν繝�繧?��繝ｳ繧貞炎髯?��
		prefSelectExtension.removePulldown();
		if ( index && select[0] ) {
			// 驕ｸ謚槭?�?繧後◆蛟､繧痴elect隕∫?��?��縺?��蜿肴�?
			var optLen = select[0].options.length;
			for ( var i = 0; i < optLen; i++ ) {
				if ( select[0].options[i].value == index ) {
					select[0].selectedIndex = i;
				}
			}
			// focus繧堤?��?��蜍�
			select[0].focus();
			// onchange繧?��繝吶Φ繝医?��蜻?��縺?���?��縺?��
			if ( select[0].onchange ) {
				select[0].onchange();
			}
		}
	},
	//-----------------------------------------
	// 逍台?��?��繝励Ν繝�繧?��繝ｳ繧貞炎髯?��縺吶?���?��域眠隕上�励Ν繝�繧?��繝ｳ逕滓�先�?�?��?��?��
	//-----------------------------------------
	removePulldownInCreate : function ( serial ) {
		var state = false;
		// 譌｢縺?��陦?��遉ｺ荳?��縺?��menu縺後≠繧後�?��蜑企勁縺吶?�?
		if ( prefSelectExtension.conf.viewingSerial ) {
			var viewingSerial = prefSelectExtension.conf.viewingSerial;
			document.body.removeChild(prefSelectExtension.conf.viewingMenu);
			prefSelectExtension.conf.viewingMenu = null;
			document.body.removeChild(prefSelectExtension.conf.viewingShim);
			prefSelectExtension.conf.viewingShim = null;
			if ( prefSelectExtension.isUnderIE7() ) {
				prefSelectExtension.changeSelectDisabled( prefSelectExtension.conf.viewingSerial, false);
			}
			prefSelectExtension.conf.viewingSerial = null;
			if ( viewingSerial == serial ) {
				// 繧?��繝ｪ繝�繧?��縺輔ｌ縺溯?��∫?��?��縺後�励Ν繝�繧?��繝ｳ陦?��遉ｺ荳?��縺?��select隕∫?��?��縺?��縺�??��縺?��true繧偵そ繝�繝�
				state = true;
			}
		}
		return state;
	},
	//-----------------------------------------
	// 逍台?��?��繝励Ν繝�繧?��繝ｳ繧貞炎髯?��縺吶?�?
	//-----------------------------------------
	removePulldown : function( event ) {
		var element;
		// 繧?��繝吶Φ繝育匱逕溷?��?��縺?��隕∫?��?��繧貞叙蠕�
		if ( event && event.target ) {
			element = event.target;
		} else if ( window.event && window.event.srcElement ) {
			element = window.event.srcElement;
		}
		if ( element ) {
			// 謫?��莨?��繝励Ν繝�繧?��繝ｳ縺後う繝吶Φ繝育匱逕溷?��?��縺?��蝣?��蜷医?��?��謚懊?�?繧?��
			if ( prefSelectExtension.checkEventTarget(element) ) {
				if ( prefSelectExtension.isFirefox() ) {
					// 繧?��繝吶Φ繝医?��繧?��繝｣繝ｳ繧?��繝ｫ
					event.returnValue = false;
					event.cancelBubble = true;
				}
				return;
			}
			// layer縺後う繝吶Φ繝育匱逕溷?��?��縺?��蝣?��蜷医?��?��謚懊?�?繧?��
			if ( !prefSelectExtension.isUnderIE7() ) {
				var str = prefSelectExtension.conf.shimSerial.split('_');
				var divs = prefSelectExtension.getTargetElements('div', str[0]);
				var len = divs.length;
				for ( var i = 0; i < len; i++ ) {
					if (  element == divs[i] ) {
						// 繧?��繝吶Φ繝医?��繧?��繝｣繝ｳ繧?��繝ｫ
						event.returnValue = false;
						event.cancelBubble = true;
						return;
					}
				}
			}
		}
		// 謫?��莨?��繝励Ν繝�繧?��繝ｳ繧貞炎髯?��
		if ( prefSelectExtension.conf.viewingSerial ) {
			document.body.removeChild(prefSelectExtension.conf.viewingMenu);
			prefSelectExtension.conf.viewingMenu = null;
			document.body.removeChild(prefSelectExtension.conf.viewingShim);
			prefSelectExtension.conf.viewingShim = null;
			if ( prefSelectExtension.isUnderIE7() ) {
				prefSelectExtension.changeSelectDisabled( prefSelectExtension.conf.viewingSerial, false);
			}
			prefSelectExtension.conf.viewingSerial = null;
		}
	},
	//-----------------------------------------
	// select隕∫?��?��縺?��陲?��縺帙ｋlayer繧堤函謌�?��?繧?��
	//-----------------------------------------
	createLayer : function( top, left, width, height ) {
		// div隕∫?��?��繧堤函謌�
		var div = document.createElement('div');
		// div隕∫?��?��縺?��繧?��繝ｪ繧?��繝ｫ繧�?��伜�??
		div.className += ' ' + prefSelectExtension.getShimSerial();
		// 蝓ｺ譛ｬ繧?��繧?��繧?��繝ｫ險?��螳?��
		div.style.zIndex = '200';
		// 騾乗�主?��?��險?��螳?��
		if ( !prefSelectExtension.isIE8() ) {
			div.style.opacity = .00;
			div.style.filter = 'alpha(opacity=0)';
			div.alpha = 0;
		}
		// 繧?��繧?��繧?��險?��螳?��
		div.style.width = width + 'px';
		div.style.height = ( height + 2) + 'px';
		// 繝昴ず繧?��繝ｧ繝ｳ險?��螳?��
		div.style.position = 'absolute';
		div.style.top = top + 'px';
		div.style.left = left + 'px';
		// 繧?��繝吶Φ繝医↓髢?��謨?��繧�?��伜�??
		div.onmousedown = function() {
			var str = this.className.split('_');
			prefSelectExtension.createPulldown(str[1]);
		}
		// html縺?��蜿肴�?
		document.body.appendChild(div);
	},
	//-----------------------------------------
	// shim繧堤函謌�?��?繧?��
	//-----------------------------------------
	createShim : function( top, left, width, height, flg ) {
		// iframe隕∫?��?��繧堤函謌�
		var iframe = document.createElement('iframe');
		// id繧定ｨ?��螳?��
		if ( flg ) {
			iframe.id = prefSelectExtension.conf.idBaseStr + '_shim';
		}
		// 蝓ｺ譛ｬ繧?��繧?��繧?��繝ｫ險?��螳?��
		iframe.setAttribute('frameBorder', '0');
		iframe.setAttribute('scrolling', 'no');
		iframe.style.border = 'none';
		iframe.style.zIndex = '100';
		// 騾乗�主?��?��險?��螳?��
		if ( !prefSelectExtension.isUnderIE7() ) {
			iframe.style.opacity = .00;
			iframe.style.filter = 'alpha(opacity=0)';
			iframe.alpha = 0;
			iframe.allowTransparency = 'true';
		}
		// 繧?��繧?��繧?��險?��螳?��
		iframe.style.width = width + 'px';
		if ( flg ) {
			iframe.style.height = height + 'px';
		} else {
			iframe.style.height = ( height + 2 ) + 'px';
		}
		// 繝昴ず繧?��繝ｧ繝ｳ險?��螳?��
		iframe.style.position = 'absolute';
		iframe.style.top = top + 'px';
		iframe.style.left = left + 'px';
		// html縺?��蜿肴�?
		document.body.appendChild(iframe);
		if ( flg ) {
			prefSelectExtension.conf.viewingShim = iframe;
		}
	},
	//-----------------------------------------
	// select隕∫?��?��縺?��disabled繧貞�?��繧頑崛縺医?���?��?��IE7莉･荳九�?��縺?��菴?��逕ｨ?��?��?��
	//-----------------------------------------
	changeSelectDisabled : function( serial, state ) {
		var select = prefSelectExtension.getTargetElements( 'select', serial );
		var len = select.length;
		for (var i = 0; i < len; i++) {
			select[i].disabled = state;
		}
	},
	//-----------------------------------------
	// 繧?��繝吶Φ繝育匱逕溷?��?��縺梧闘莨?��繝励Ν繝�繧?��繝ｳ縺九ｒ蛻?��螳壹�?繧?��
	//-----------------------------------------
	checkEventTarget : function( element ) {
		var result = false;
		if ( element.nodeType == '1' && element.className.match(prefSelectExtension.conf.idBaseStr) ) {
			result = true;
		}
		return result;
	},
	//-----------------------------------------
	// select隕∫?��?��縺?��optgroup縺悟�?�縺?��繧後ｋ縺九ｒ霑斐�?
	//-----------------------------------------
	existOptgroup : function( element ) {
		var optgroup = element.getElementsByTagName('OPTGROUP');
		if ( optgroup.length && optgroup.length > 0 ) {
			return true;
		} else {
			return false;
		}
	},
	//-----------------------------------------
	// selected螻樊�?��縺梧�?螳壹?�?繧後�?�縺?��繧黍ption隕∫?��?��縺?��逵悟錐繧定ｿ斐�?
	//-----------------------------------------
	getSelectedOption : function( element ) {
		var result = false;
		var opt = element.getElementsByTagName('OPTION');
		var len = opt.length;
		for (var i = 0; i < len; i++) {
			if ( opt[i].selected ) {
				result = opt[i].innerHTML;
				break;
			}
		}
		return result;
	},
	//-----------------------------------------
	// 逵悟錐縺九ｉvalue繧貞叙蠕励?�?縺?��霑斐�?
	//-----------------------------------------
	getOptionValueFromPrefName : function( element, prefName ) {
		var result = false;
		var opt = element.getElementsByTagName('OPTION');
		var len = opt.length;
		for (var i = 0; i < len; i++) {
			if ( opt[i].innerHTML.match(prefName) ) {
				result = opt[i].getAttribute('value');
				break;
			}
		}
		return result;
	},
	//-----------------------------------------
	// select隕∫?��?��逕ｨ繧?��繝ｪ繧?��繝ｫ繧定ｿ斐�?
	//-----------------------------------------
	getSelectSerial : function() {
		return prefSelectExtension.conf.selectSerial.replace( '###', prefSelectExtension.conf.count );
	},
	//-----------------------------------------
	// shim逕ｨ繧?��繝ｪ繧?��繝ｫ繧定ｿ斐�?
	//-----------------------------------------
	getShimSerial : function() {
		return prefSelectExtension.conf.shimSerial.replace( '###', prefSelectExtension.conf.count );
	},
	//-----------------------------------------
	// �?��雎｡隕∫?��?��縺?��繧?��繧?��繧?��繧貞叙蠕励�?繧?��
	//-----------------------------------------
	getElementSize : function( element ) {
		var w = element.offsetWidth;
		var h = element.offsetHeight;
		return ({ 'width': w, 'height': h });
	},
	//-----------------------------------------
	// 繝悶Λ繧?��繧?��縺?��逕ｻ髱?��繧?��繧?��繧?��繧貞叙蠕励�?繧?��
	//-----------------------------------------
	getBrowserSize : function() {
		var w = 0;
		var h = 0;
		if ( window.innerWidth ) {
			w = window.innerWidth;
		} else if ( document.documentElement && document.documentElement.clientWidth != 0 ) {
			w = document.documentElement.clientWidth;
		} else if ( document.body ) {
			w = document.body.clientWidth;
		}
		w = (document.documentElement.scrollLeft || document.body.scrollLeft) + w;
		if ( window.innerHeight ) {
			h = window.innerHeight;
		} else if ( document.documentElement && document.documentElement.clientHeight != 0 ) {
			h = document.documentElement.clientHeight;
		} else if ( document.body ) {
			h = document.body.clientHeight;
		}
		h = (document.documentElement.scrollTop || document.body.scrollTop) + h;
		return ({ 'width': w, 'height': h });
	},
	//-----------------------------------------
	// �?��雎｡隕∫?��?��縺?��繝昴ず繧?��繝ｧ繝ｳ繧貞叙蠕励�?繧?��
	//-----------------------------------------
	getElementPosition : function( element ) {
		var offsetTrail = (typeof element == 'string') ? document.getElementById(element): element;
		var x = 0;
		var y = 0;
		while (offsetTrail) {
			x += offsetTrail.offsetLeft;
			y += offsetTrail.offsetTop;
			offsetTrail = offsetTrail.offsetParent;
		}
		if ( navigator.userAgent.indexOf('Mac') != -1 && typeof doc.body.leftMargin != 'undefined' ) {
			x += document.body.leftMargin;
			y += document.body.topMargin;
		}
		return ({ 'left': x, 'top': y });
	},
	//-----------------------------------------
	// 繧?��繝ｼ繧?��繝�繝医ち繧?��繧貞叙蠕励�?繧?��
	//-----------------------------------------
	getTargetElements : function( tag, cls ) {
		var elements = new Array();
		var targetElements = document.getElementsByTagName(tag.toUpperCase());
		var len = targetElements.length;
		for ( var i = 0; i < len; i++ ) {
			if ( targetElements[i].className.match(cls) ) {
				elements[elements.length] = targetElements[i];
			}
		}
		return elements;
	},
	//-----------------------------------------
	// IE蛻?��螳?��
	//-----------------------------------------
	isIE : function() {
		IE='\v'=='v';
		if(IE){
			return true;
		}else{
			return false;
		}
	},
	//-----------------------------------------
	// IE7莉･荳句愛螳?��
	//-----------------------------------------
	isUnderIE7 : function() {
		IE='\v'=='v';
		if ( IE ) {
			if ( document.documentMode ) {
				if ( document.documentMode == '7' || document.documentMode == '5' ) {
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} else {
			return false;
		}
	},
	//-----------------------------------------
	// IE8蛻?��螳?��
	//-----------------------------------------
	isIE8 : function() {
		IE='\v'=='v';
		if( IE && document.documentMode && document.documentMode != '7' && document.documentMode != '5' ){
			return true;
		}else{
			return false;
		}
	},
	//-----------------------------------------
	// Firefox蛻?��螳?��
	//-----------------------------------------
	isFirefox : function() {
		FF=/a/[-1]=='a';
		if(FF){
			return true;
		}else{
			return false;
		}
	},
	//-----------------------------------------
	// 繧?��繝吶Φ繝医↓髢?��謨?��繧�?��伜�?�縺吶?�?
	//-----------------------------------------
	addEvent : function( target, event, func ) {
		try {
			target.addEventListener(event, func, false);
		} catch (e) {
			target.attachEvent('on' + event, func);
		}
	}
}
// 螳溯?��?��
prefSelectExtension.addEvent( window, 'load', prefSelectExtension.init );