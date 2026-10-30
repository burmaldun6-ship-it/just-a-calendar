#!/usr/bin/env python3
"""Destructive smoke test for a dedicated Android emulator, never a real phone.
Requires Python 3 and Android platform-tools. Resets local.minicalendar data.
Usage: python3 tests/ui_smoke.py --serial emulator-5554 --apk path/to/calendar.apk
"""
import argparse, os, pathlib, re, shutil, subprocess, tempfile, time, uuid
import xml.etree.ElementTree as ET
p=argparse.ArgumentParser();p.add_argument('--serial',default='emulator-5554');p.add_argument('--apk',required=True);p.add_argument('--out',default='ui-test-output');a=p.parse_args()
adb_exe=os.environ.get('ADB') or shutil.which('adb')
if not adb_exe:raise SystemExit('Set ADB or add Android platform-tools to PATH')
out=pathlib.Path(a.out);out.mkdir(parents=True,exist_ok=True)
def adb(*args,timeout=60,check=True):
 r=subprocess.run([adb_exe,'-s',a.serial,*args],capture_output=True,timeout=timeout)
 if check and r.returncode:raise RuntimeError(r.stderr.decode(errors='replace'))
 return r.stdout
start=time.monotonic()
while time.monotonic()-start<300:
 try:
  if adb('shell','getprop','sys.boot_completed',timeout=8).strip()==b'1':break
 except (RuntimeError,subprocess.TimeoutExpired):pass
 time.sleep(5)
else:raise SystemExit('Emulator boot timed out')
assert adb('shell','getprop','ro.kernel.qemu').strip()==b'1','Refusing to reset a real device'
adb('root',check=False);adb('wait-for-device')
adb('shell','settings','put','global','hide_error_dialogs','1')
for name in ['window_animation_scale','transition_animation_scale','animator_duration_scale']:adb('shell','settings','put','global',name,'1')
adb('shell','settings','put','system','font_scale','1')
print(adb('install','--no-incremental','--no-streaming','-r',str(pathlib.Path(a.apk).resolve()),timeout=120).decode().strip(),flush=True)
adb('shell','pm','clear','local.minicalendar')
def launch():
 adb('shell','am','start','-W','-n','local.minicalendar/.MainActivity',timeout=90);time.sleep(1)
launch();time.sleep(3)
def bounds(node):return tuple(map(int,re.findall(r'\d+',node.get('bounds'))))
def center(node):
 x,y,r,b=bounds(node);return (x+r)//2,(y+b)//2
def tap(node):
 x,y=center(node);adb('shell','input','tap',str(x),str(y));time.sleep(.4)
def dump():
 for attempt in range(7):
  path='/sdcard/calendar-'+uuid.uuid4().hex+'.xml'
  try:
   adb('shell','uiautomator','dump',path,timeout=60)
   tree=ET.fromstring(adb('exec-out','cat',path))
   if any(n.get('text')=="System UI isn't responding" for n in tree.iter('node')):
    tap(next(n for n in tree.iter('node') if n.get('text')=='Close app'));time.sleep(1);continue
   return tree
  except (RuntimeError,ET.ParseError):time.sleep(1)
  finally:adb('shell','rm','-f',path,check=False)
 raise RuntimeError('No fresh UI hierarchy; stale files are never reused')
def find(tree,**attributes):
 def matches(node,key,value):
  actual=node.get(key,'')
  if key=='text':
   if node.get('class')=='android.widget.Switch':actual=re.sub(r' (ON|OFF)$','',actual)
   return actual.casefold()==value.casefold()
  return actual==value
 return next(n for n in tree.iter('node') if all(matches(n,k,v) for k,v in attributes.items()))
def title(tree):return next(n for n in tree.iter('node') if re.fullmatch(r'[А-Я][а-я]+ \d{1,4}',n.get('text','')))
def wait(test,description):
 for _ in range(5):
  tree=dump()
  try:
   if test(tree):return tree
  except StopIteration:pass
  time.sleep(.5)
 raise AssertionError(description)
def calendar_ready(tree):
 try:
  label=title(tree).get('text');name,year=label.split()
  names=['Январь','Февраль','Март','Апрель','Май','Июнь','Июль','Август','Сентябрь','Октябрь','Ноябрь','Декабрь']
  genitive=['января','февраля','марта','апреля','мая','июня','июля','августа','сентября','октября','ноября','декабря']
  month=names.index(name);expected=' '+genitive[month]+' '+year
  cells=[n for n in tree.iter('node') if n.get('class')=='android.widget.TextView' and n.get('text','').isdigit() and n.get('content-desc')]
  return bool(cells) and any(n.get('text')=='1' for n in cells) and all(expected in n.get('content-desc') for n in cells)
 except (StopIteration,ValueError):return False
def screenshot(name):(out/name).write_bytes(adb('exec-out','screencap','-p'))
def gear(tree):return find(tree,**{'content-desc':'Настройки'})
def today_title(tree):return find(tree,text='Сегодня:')
def close_settings(tree):tap(find(tree,text='Готово'))
def prefs():return ET.fromstring(adb('exec-out','cat','/data/data/local.minicalendar/shared_prefs/MainActivity.xml'))
def pref_value(name):
 n=next(n for n in prefs() if n.get('name')==name);return n.get('value') or n.text

t=wait(calendar_ready,'Initial grid did not settle');initial=title(t).get('text');footer=bounds(today_title(t));screenshot('main-light.png')
assert not any(n.get('content-desc') in ['Следующий месяц','Предыдущий месяц'] for n in t.iter('node'))
assert any(re.fullmatch(r'[1-9]\d?\.\d{2}:\d{2}',n.get('text','')) for n in t.iter('node'))
pager=find(t,**{'resource-id':'local.minicalendar:id/month_pager'});left,top,right,bottom=bounds(pager);x1=right-40;x2=left+40;y=(top+bottom)//2
adb('shell','input','swipe',str(x1),str(y),str(x2),str(y),'1200')
t=wait(lambda tree:title(tree).get('text')!=initial,'Slow swipe failed');assert bounds(today_title(t))==footer,f'Today block moved between months: {footer} -> {bounds(today_title(t))}'
print('PASS: native paging; fixed Today position across 5/6-row months',flush=True)
# Trigger an immediate Today action while the ViewPager2 settling animation is active.
fx,fy=center(today_title(t))
adb('shell',f'input swipe {x1} {y} {x2} {y} 100; input tap {fx} {fy}')
t=wait(lambda tree:title(tree).get('text')==initial,'Today did not stop in-flight navigation');time.sleep(1)
assert title(dump()).get('text')==initial
print('PASS: Today interrupts page animation without a late month change',flush=True)
t=dump();tap(gear(t));t=wait(lambda tree:any(n.get('text')=='Интервал: 5 с' for n in tree.iter('node')),'Default interval is not 5 seconds');screenshot('settings-light.png')
tap(find(t,text='Тёмная тема'))
t=wait(lambda tree:find(tree,text='Тёмная тема').get('checked')=='true','Dark theme/reopened settings failed');screenshot('settings-dark.png')
seek=find(t,**{'class':'android.widget.SeekBar'});sx,sy,sr,sb=bounds(seek);adb('shell','input','tap',str(sr-2),str((sy+sb)//2))
t=wait(lambda tree:any(n.get('text')=='Интервал: 60 с' for n in tree.iter('node')),'Cannot select 60 seconds')
tap(find(t,text='Обновлять дату'))
t=wait(lambda tree:find(tree,text='Обновлять дату').get('checked')=='false','Auto update did not switch off')
assert find(t,**{'class':'android.widget.SeekBar'}).get('enabled')=='false';assert pref_value('auto_update')=='false';assert pref_value('interval_seconds')=='60'
screenshot('settings-auto-off.png');close_settings(t);t=dump();screenshot('main-dark.png')
print('PASS: settings; theme; 5-second default; 60-second maximum; auto-off; saved values',flush=True)
adb('shell','am','force-stop','local.minicalendar');launch();t=dump();tap(gear(t));t=dump()
assert find(t,text='Тёмная тема').get('checked')=='true';assert find(t,text='Обновлять дату').get('checked')=='false'
tap(find(t,text='Обновлять дату'));t=dump();seek=find(t,**{'class':'android.widget.SeekBar'});sx,sy,sr,sb=bounds(seek);adb('shell','input','tap',str(sx+2),str((sy+sb)//2))
t=wait(lambda tree:any(n.get('text')=='Интервал: 1 с' for n in tree.iter('node')),'Cannot select 1 second');close_settings(t)
print('PASS: persistence after restart; minimum interval 1 second',flush=True)
adb('shell','settings','put','system','accelerometer_rotation','0');adb('shell','settings','put','system','user_rotation','1');time.sleep(2)
t=wait(calendar_ready,'Landscape grid did not settle');assert title(t).get('text')==initial;screenshot('landscape.png')
adb('shell','input','swipe','8','330','8','100','500');time.sleep(1)
t=dump();assert today_title(t).get('bounds')!='[0,0][0,0]';screenshot('landscape-scrolled.png')
adb('shell','settings','put','system','user_rotation','0');adb('shell','settings','put','system','font_scale','2');time.sleep(2)
t=wait(calendar_ready,'Large-font grid did not settle');screenshot('large-font-top.png');adb('shell','input','swipe','8','700','8','200','500');time.sleep(1);screenshot('large-font-bottom.png')
adb('shell','settings','put','system','font_scale','1');adb('shell','wm','size','320x720');time.sleep(2);t=wait(calendar_ready,'Narrow grid did not settle');screenshot('narrow-main.png')
adb('shell','wm','size','reset')
print('PASS: rotation and large-font/narrow-window rendering captured for inspection',flush=True)
# Deliberately malformed local preference types must fall back, not crash.
adb('shell','am','force-stop','local.minicalendar')
with tempfile.NamedTemporaryFile(mode='w',suffix='.xml',delete=False) as f:
 f.write('<?xml version="1.0" encoding="utf-8"?><map><string name="dark">invalid</string><string name="auto_update">invalid</string><string name="interval_seconds">invalid</string></map>');tmp=f.name
try:
 adb('push',tmp,'/data/local/tmp/calendar-test-preferences.xml')
 adb('shell','cp','/data/local/tmp/calendar-test-preferences.xml','/data/data/local.minicalendar/shared_prefs/MainActivity.xml')
finally:pathlib.Path(tmp).unlink();adb('shell','rm','-f','/data/local/tmp/calendar-test-preferences.xml',check=False)
launch();t=dump();tap(gear(t));t=dump();assert any(n.get('text')=='Интервал: 5 с' for n in t.iter('node'));assert find(t,text='Обновлять дату').get('checked')=='true'
log=adb('logcat','-d','-b','events',timeout=20).decode(errors='replace')
assert not any('am_anr' in line and 'local.minicalendar' in line for line in log.splitlines())
log=adb('logcat','-d','-s','AndroidRuntime:E',timeout=20).decode(errors='replace');assert 'FATAL EXCEPTION' not in log,log[-3000:]
adb('shell','settings','put','global','hide_error_dialogs','0')
print('PASS: malformed preferences recover safely; no calendar ANR or runtime crash. Animations remained enabled.',flush=True)
