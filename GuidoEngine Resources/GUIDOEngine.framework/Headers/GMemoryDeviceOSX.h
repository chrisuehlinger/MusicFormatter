#ifndef GMemoryDeviceOSX_H
#define GMemoryDeviceOSX_H

/*
	GUIDO Library
	Copyright (C) 2002  Holger Hoos, Juergen Kilian, Kai Renz
	Copyright (C) 2003--2006  Grame

	This library is free software; you can redistribute it and/or
	modify it under the terms of the GNU Lesser General Public
	License as published by the Free Software Foundation; either
	version 2.1 of the License, or (at your option) any later version.

	This library is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
	Lesser General Public License for more details.

	You should have received a copy of the GNU Lesser General Public
	License along with this library; if not, write to the Free Software
	Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

*/

/////////////////////////////////////////////////////////////////
///
/// 	MacOS X Quartz 2D implementation of VGDevice.
///
/////////////////////////////////////////////////////////////////
#include "GDeviceOSX.h"

// --------------------------------------------------------------
class_export GMemoryDeviceOSX : public GDeviceOSX
{
	public:
								
								GMemoryDeviceOSX( int inWidth, int inHeight, VGSystem* sys );	
		virtual					~GMemoryDeviceOSX();
		
		// - Generic services ------------------------------------------
		virtual void			NotifySize( int inWidth, int inHeight );
		virtual void*			GetBitMapPixels() const;

		// - OSX specific services -------------------------------------
				void			PutImage (CGImageRef img);		
		
	protected:
	
				CGContextRef	CreateBitmapContext(int inWidth, int inHeight);
				void*			mOffscreen;		
};
			

#endif



