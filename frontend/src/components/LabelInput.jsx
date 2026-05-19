import { forwardRef } from 'react';
import './LabelInput.css';

const LabelInput = forwardRef(({children, inputType, placeholerText}, ref) => {
	return (
		<div className="label-input">
			<label>{children}</label>
			<input 
				type={inputType}
				placeholder={placeholerText}
				ref={ref}
			/>
		</div>
	);
});

export default LabelInput;
